package de.blazemcworld.fireflow.node.impl.event;

import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.evaluation.CodeEvaluator;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.node.annotation.FlowContext;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.entity.Player;
import net.minestom.server.event.entity.EntityAttackEvent;

public class PlayerPunchPlayerEventNode extends Node {

    private final NodeOutput signal;

    public PlayerPunchPlayerEventNode() {
        super("Player Punch Player");

        signal = output("Signal", SignalValue.INSTANCE);
        output("Player", PlayerValue.INSTANCE);
        output("Other", PlayerValue.INSTANCE);

        loadJava(PlayerPunchPlayerEventNode.class);
    }

    @FlowValueOutput("Player")
    private static Object player() {
        return ctx().getInternalVar("ID$player");
    }

    @FlowValueOutput("Other")
    private static Object other() {
        return ctx().getInternalVar("ID$other");
    }

    @FlowContext
    private static CompiledNode ctx() {
        throw new IllegalStateException();
    }

    @Override
    public void register(CodeEvaluator evaluator) {
        String entrypoint = evaluator.compiler.markRoot(signal);
        evaluator.events.addListener(EntityAttackEvent.class, event -> {
            if (event.getEntity() instanceof Player player) {
                if (event.getTarget() instanceof Player other) {
                    CompiledNode context = evaluator.newContext();
                    context.setInternalVar(allocateId("player"), new PlayerValue.Reference(evaluator.space, player));
                    context.setInternalVar(allocateId("other"), new PlayerValue.Reference(evaluator.space, other));
                    context.emit(entrypoint);
                }
            }
        });
    }
}
