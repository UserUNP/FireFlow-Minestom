package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

public class SendActionBarNode extends Node {
    public SendActionBarNode() {
        super("Send Action Bar");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Message", MessageValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(SendActionBarNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) p.sendActionBar(message());
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Message")
    private static Component message() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }
}