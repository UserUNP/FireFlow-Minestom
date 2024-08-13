package de.blazemcworld.fireflow.space;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.evaluation.CodeEvaluator;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.NetworkBuffer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Space {

    public final InstanceContainer play;
    public final InstanceContainer code;
    public final Task saveTask;
    public final SpaceInfo info;
    private boolean isUnused = false;
    private final CodeEditor editor;
    public CodeEvaluator evaluator;
    public Map<String, Object> variables = new HashMap<>();

    public Space(SpaceInfo info) {
        this.info = info;

        InstanceManager manager = MinecraftServer.getInstanceManager();
        play = manager.createInstanceContainer();
        code = manager.createInstanceContainer();

        play.setChunkLoader(new AnvilLoader("spaces/" + info.id));

        play.setChunkSupplier(LightingChunk::new);
        code.setChunkSupplier(LightingChunk::new);

        play.setGenerator(unit -> {
            if (Math.abs(unit.absoluteStart().x() + 8) > 16) return;
            if (Math.abs(unit.absoluteStart().z() + 8) > 16) return;
            unit.modifier().fillHeight(-1, 0, Block.SMOOTH_STONE);
        });

        code.setGenerator(unit -> {
            if (unit.absoluteStart().z() != 16.0) return;
            unit.modifier().fill(
                    new BlockVec(0, 0, 0).add(unit.absoluteStart()),
                    new BlockVec(16, 128, 1).add(unit.absoluteStart()),
                    Block.POLISHED_BLACKSTONE
            );
        });

        play.setTimeRate(0);
        code.setTimeRate(0);

        EventNode<InstanceEvent> playEvents = play.eventNode();
        EventNode<InstanceEvent> codeEvents = code.eventNode();

        playEvents.addListener(PlayerSpawnEvent.class, event -> {
            isUnused = false;
        });
        codeEvents.addListener(PlayerSpawnEvent.class, event -> {
            isUnused = false;
        });

        readVariables();
        editor = new CodeEditor(this);
        evaluator = new CodeEvaluator(this, editor);

        saveTask = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            if (isUnused) {
                unregister();
                SpaceManager.forget(info.id);
                return TaskSchedule.stop();
            }
            if (play.getPlayers().isEmpty() && code.getPlayers().isEmpty()) {
                isUnused = true;
            }
            save();
            return TaskSchedule.minutes(1);
        }, TaskSchedule.minutes(1));
    }

    private void saveVariables() {
        NetworkBuffer buffer = new NetworkBuffer();

        List<Object> objects = new ArrayList<>();
        collectObjects(objects, variables.values());

        buffer.write(NetworkBuffer.INT, objects.size());
        for (Object obj : objects) {
            if (obj instanceof Double d) {
                buffer.write(NetworkBuffer.BYTE, (byte) 0);
                buffer.write(NetworkBuffer.DOUBLE, d);
            } else if (obj instanceof String s) {
                buffer.write(NetworkBuffer.BYTE, (byte) 1);
                buffer.write(NetworkBuffer.STRING, s);
            } else if (obj instanceof Boolean b) {
                buffer.write(NetworkBuffer.BYTE, (byte) 2);
                buffer.write(NetworkBuffer.BOOLEAN, b);
            } else if (obj instanceof Component c) {
                buffer.write(NetworkBuffer.BYTE, (byte) 3);
                buffer.write(NetworkBuffer.STRING, MessageValue.MM.serialize(c));
            } else if (obj instanceof List<?> l) {
                buffer.write(NetworkBuffer.BYTE, (byte) 4);
                buffer.write(NetworkBuffer.INT, l.size());
                for (Object v : l) {
                    buffer.write(NetworkBuffer.INT, objects.indexOf(v));
                }
            } else if (obj instanceof PlayerValue.Reference ref) {
                buffer.write(NetworkBuffer.BYTE, (byte) 5);
                buffer.write(NetworkBuffer.UUID, ref.uuid());
            } else if (obj instanceof Pos pos) {
                buffer.write(NetworkBuffer.BYTE, (byte) 6);
                buffer.write(NetworkBuffer.DOUBLE, pos.x());
                buffer.write(NetworkBuffer.DOUBLE, pos.y());
                buffer.write(NetworkBuffer.DOUBLE, pos.z());
                buffer.write(NetworkBuffer.FLOAT, pos.yaw());
                buffer.write(NetworkBuffer.FLOAT, pos.pitch());
            } else if (obj instanceof Vec vec) {
                buffer.write(NetworkBuffer.BYTE, (byte) 7);
                buffer.write(NetworkBuffer.DOUBLE, vec.x());
                buffer.write(NetworkBuffer.DOUBLE, vec.y());
                buffer.write(NetworkBuffer.DOUBLE, vec.z());
            } else if (obj instanceof Map<?, ?> map) {
                buffer.write(NetworkBuffer.BYTE, (byte) 8);
                buffer.write(NetworkBuffer.INT, map.size());

                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    buffer.write(NetworkBuffer.INT, objects.indexOf(entry.getKey()));
                    buffer.write(NetworkBuffer.INT, objects.indexOf(entry.getValue()));
                }
            }
        }

        buffer.write(NetworkBuffer.INT, variables.size());
        for (Map.Entry<String, Object> var : variables.entrySet()) {
            buffer.write(NetworkBuffer.STRING, var.getKey());
            buffer.write(NetworkBuffer.INT, objects.indexOf(var.getValue()));
        }

        try {
            Path p = Path.of("spaces").resolve(String.valueOf(info.id)).resolve("variables.bin");
            if (!Files.exists(p.getParent())) Files.createDirectories(p.getParent());
            Files.write(p, buffer.readBytes(buffer.writeIndex()));
        } catch (Exception err) {
            FireFlow.LOGGER.error("Error saving variables to file!", err);
        }
    }

    private void collectObjects(List<Object> out, Collection<Object> todo) {
        for (Object each : todo) {
            if (!out.contains(each)) out.add(each);
            if (each instanceof List<?> l) {
                collectObjects(out, (Collection<Object>) l);
            }
            if (each instanceof Map<?, ?> m) {
                collectObjects(out, (Collection<Object>) m.keySet());
                collectObjects(out, (Collection<Object>) m.values());
            }
        }
    }

    private void readVariables() {
        NetworkBuffer buffer;
        try {
            Path p = Path.of("spaces").resolve(String.valueOf(info.id)).resolve("variables.bin");
            if (!Files.exists(p)) return;
            buffer = new NetworkBuffer(ByteBuffer.wrap(Files.readAllBytes(p)));
        } catch (Exception err) {
            FireFlow.LOGGER.error("Error reading variables from file!", err);
            return;
        }

        variables.clear();

        int count = buffer.read(NetworkBuffer.INT);
        List<Object> objects = new ArrayList<>();
        List<Runnable> connect = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            byte type = buffer.read(NetworkBuffer.BYTE);
            switch (type) {
                case 0 -> objects.add(buffer.read(NetworkBuffer.DOUBLE));
                case 1 -> objects.add(buffer.read(NetworkBuffer.STRING));
                case 2 -> objects.add(buffer.read(NetworkBuffer.BOOLEAN));
                case 3 -> objects.add(MessageValue.MM.deserialize(buffer.read(NetworkBuffer.STRING)));
                case 4 -> {
                    List<Object> list = new ArrayList<>();
                    int size = buffer.read(NetworkBuffer.INT);
                    List<Integer> ids = new ArrayList<>();
                    for (int j = 0; j < size; j++) {
                        ids.add(buffer.read(NetworkBuffer.INT));
                    }
                    connect.add(() -> {
                        for (int id : ids) {
                            list.add(objects.get(id));
                        }
                    });
                    objects.add(list);
                }
                case 5 -> objects.add(new PlayerValue.Reference(this, buffer.read(NetworkBuffer.UUID)));
                case 6 -> {
                    double x = buffer.read(NetworkBuffer.DOUBLE);
                    double y = buffer.read(NetworkBuffer.DOUBLE);
                    double z = buffer.read(NetworkBuffer.DOUBLE);
                    float yaw = buffer.read(NetworkBuffer.FLOAT);
                    float pitch = buffer.read(NetworkBuffer.FLOAT);
                    objects.add(new Pos(x, y, z, yaw, pitch));
                }
                case 7 -> {
                    double x = buffer.read(NetworkBuffer.DOUBLE);
                    double y = buffer.read(NetworkBuffer.DOUBLE);
                    double z = buffer.read(NetworkBuffer.DOUBLE);
                    objects.add(new Vec(x, y, z));
                }
                case 8 -> {
                    Map<Object, Object> map = new HashMap<>();
                    int size = buffer.read(NetworkBuffer.INT);

                    List<Pair<Integer, Integer>> pairs = new ArrayList<>();
                    for (int j = 0; j < size; j++) {
                        int keyId = buffer.read(NetworkBuffer.INT);
                        int valueId = buffer.read(NetworkBuffer.INT);

                        pairs.add(Pair.of(keyId, valueId));
                    }

                    connect.add(() -> {
                        for (Pair<Integer, Integer> pair : pairs) {
                            map.put(objects.get(pair.left()), objects.get(pair.right()));
                        }
                    });
                    objects.add(map);
                }
            }
        }

        for (Runnable task : connect) task.run();

        count = buffer.read(NetworkBuffer.INT);
        for (int i = 0; i < count; i++) {
            String name = buffer.read(NetworkBuffer.STRING);
            int id = buffer.read(NetworkBuffer.INT);
            variables.put(name, objects.get(id));
        }
    }

    public void unregister() {
        saveTask.cancel();
        save();

        MinecraftServer.getInstanceManager().unregisterInstance(play);
        MinecraftServer.getInstanceManager().unregisterInstance(code);
    }

    private void save() {
        play.saveChunksToStorage();
        saveVariables();
        editor.save();
    }

    public void reload() {
        evaluator.stop(true);
        evaluator = new CodeEvaluator(this, editor);
    }
}
