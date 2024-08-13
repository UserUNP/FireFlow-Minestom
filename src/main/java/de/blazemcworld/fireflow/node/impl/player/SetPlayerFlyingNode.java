package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.entity.Player;

public class SetPlayerFlyingNode extends Node {
    public SetPlayerFlyingNode() {
        super("Set Player Flying");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("State", ConditionValue.INSTANCE).withDefault(true);
        output("Next", SignalValue.INSTANCE);

        loadJava(SetPlayerFlyingNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) p.setFlying(state());
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player() {
        throw new IllegalStateException();
    }

    @FlowValueInput("State")
    private static boolean state() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }
}