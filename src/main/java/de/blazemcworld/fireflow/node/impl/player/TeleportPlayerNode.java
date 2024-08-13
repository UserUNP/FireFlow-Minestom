package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.PositionValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class TeleportPlayerNode extends Node {

    public TeleportPlayerNode() {
        super("Teleport Player");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Position", PositionValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(TeleportPlayerNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run() {
        Player p = player().resolve();
        if (p != null) {
            Pos pos = position();
            if (Math.abs(pos.x()) < 999999 && Math.abs(pos.y()) < 999999 && Math.abs(pos.z()) < 999999) {
                p.teleport(pos);
            }
        }
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Position")
    private static Pos position() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }

}
