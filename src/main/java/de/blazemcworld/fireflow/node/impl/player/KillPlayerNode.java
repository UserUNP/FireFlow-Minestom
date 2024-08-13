package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.entity.Player;

public class KillPlayerNode extends Node {
    public KillPlayerNode() {
        super("Kill Player");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(KillPlayerNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) p.kill();
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }
}