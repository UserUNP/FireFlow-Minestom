package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.PositionValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class KnockBackPlayerNode extends Node {

    public KnockBackPlayerNode() {
        super("Knockback Player");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Origin", PositionValue.INSTANCE);
        input("Strength", NumberValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(KnockBackPlayerNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) {
            Pos origin = origin();
            p.takeKnockback((float) strength(), origin.x(), origin.z());
        }
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

    @FlowValueInput("Origin")
    private static Pos origin() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Strength")
    private static double strength() {
        throw new IllegalStateException();
    }

}
