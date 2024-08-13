package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import net.minestom.server.entity.Player;

public class SetPlayerFireTicksNode extends Node {

    public SetPlayerFireTicksNode() {
        super("Set Player Fire Ticks");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Ticks", NumberValue.INSTANCE).withDefault(0);
        output("Next", SignalValue.INSTANCE);
    }

    @FlowSignalInput("Signal")
    private static void run()  {
        Player p = player().resolve();
        if (p != null) p.setFireTicks((int) ticks());
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player()   {
        throw new IllegalStateException();
    }

    @FlowValueInput("Ticks")
    private static double ticks()  {
        throw new IllegalStateException();
    }


    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }

}
