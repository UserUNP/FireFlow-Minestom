package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.entity.Player;

public class SetPlayerHealthNode extends Node {
    public SetPlayerHealthNode() {
        super("Set Player Health");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Health", NumberValue.INSTANCE).withDefault(20);
        output("Next", SignalValue.INSTANCE);

        loadJava(SetPlayerHealthNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) p.setHealth((float) health());
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Health")
    private static double health() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }
}