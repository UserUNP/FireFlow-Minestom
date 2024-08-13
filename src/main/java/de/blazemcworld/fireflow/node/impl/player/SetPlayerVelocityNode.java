package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public class SetPlayerVelocityNode extends Node {

    public SetPlayerVelocityNode() {
        super("Set Player Velocity");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Velocity", VectorValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(SetPlayerVelocityNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) {
            p.setVelocity(velocity());
        }
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Velocity")
    private static Vec velocity() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }

}
