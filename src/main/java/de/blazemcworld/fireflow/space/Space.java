package de.blazemcworld.fireflow.space;

import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.evaluation.CodeEvaluator;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public class Space {

    public final InstanceContainer play;
    public final InstanceContainer code;
    public final Task saveTask;
    public final SpaceInfo info;
    private boolean isUnused = false;
    private final CodeEditor editor;
    private CodeEvaluator evaluator;

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

    public void unregister() {
        saveTask.cancel();
        save();

        MinecraftServer.getInstanceManager().unregisterInstance(play);
        MinecraftServer.getInstanceManager().unregisterInstance(code);
    }

    private void save() {
        play.saveChunksToStorage();
        editor.save();
    }

    public void reload() {
        evaluator.stop(true);
        evaluator = new CodeEvaluator(this, editor);
    }
}
