package de.blazemcworld.fireflow.evaluation;

import de.blazemcworld.fireflow.compiler.ByteClassLoader;
import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.util.Config;
import de.blazemcworld.fireflow.util.Messages;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.instance.InstanceTickEvent;
import net.minestom.server.event.trait.InstanceEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class CodeEvaluator {

    public CodeEditor editor;
    public EventNode<InstanceEvent> events;
    public NodeCompiler compiler;
    public final Space space;
    private final List<Runnable> prepare = new ArrayList<>();
    private final List<Runnable> compile = new ArrayList<>();
    private final Class<CompiledNode> compiledClass;
    public long cpuLeft = Config.store.limits().cpuPerTick();
    private boolean stopped = false;

    public CodeEvaluator(Space space, CodeEditor editor) {
        this.editor = editor;
        this.space = space;
        events = EventNode.type("space-" + space.info.id, EventFilter.INSTANCE);
        space.play.eventNode().addChild(events);
        List<Node> nodes = editor.getNodes();
        compiler = new NodeCompiler("Space" + space.info.id);
        for (Node node : nodes) {
            node.register(this);
        }
        for (Runnable r : prepare) r.run();
        for (Runnable r : compile) r.run();
        prepare.clear();
        compile.clear();
        compiledClass = (Class<CompiledNode>) new ByteClassLoader(CodeEvaluator.class.getClassLoader()).define(compiler.className, compiler.compile());

        events.addListener(InstanceTickEvent.class, event -> {
            cpuLeft = Config.store.limits().cpuPerTick();
        });
    }

    public void prepare(Runnable r) {
        prepare.add(r);
    }

    public void compile(Runnable r) {
        compile.add(r);
    }

    public void stop(boolean reload) {
        if (stopped) return;
        stopped = true;
        if (!reload) {
            for (Player player : space.play.getPlayers()) {
                player.sendMessage(Messages.error("Space code evaluation has been halted!"));
            }
            for (Player player : space.code.getPlayers()) {
                player.sendMessage(Messages.error("Space code evaluation has been halted!"));
            }
        }
        space.play.eventNode().removeChild(events);
    }

    public CompiledNode newContext() {
        try {
            CompiledNode ctx = compiledClass.getDeclaredConstructor().newInstance();
            ctx.evaluator = this;
            return ctx;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
