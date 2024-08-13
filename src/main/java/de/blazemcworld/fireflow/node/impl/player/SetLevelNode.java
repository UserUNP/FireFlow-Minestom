package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.entity.Player;

public class SetLevelNode extends Node {
    public SetLevelNode() {
        super("Set Level");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Level", NumberValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(SetLevelNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) p.setLevel((int) level());
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Level")
    private static double level() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }
}