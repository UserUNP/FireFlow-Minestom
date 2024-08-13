package de.blazemcworld.fireflow.node.impl.player;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class SetGamemodeNode extends Node {
    public SetGamemodeNode() {
        super("Set Gamemode");

        input("Signal", SignalValue.INSTANCE);
        input("Player", PlayerValue.INSTANCE);
        input("Gamemode", TextValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(SetGamemodeNode.class);
    }

    @FlowSignalInput("Signal")
    private static void run()  {
        Player p = player().resolve();
        if (p != null) {
            String n = gamemode().toUpperCase();
            for (GameMode g : GameMode.values()) {
                if (n.equals(g.name())) {
                    p.setGameMode(g);
                }
            }
        }
        next();
    }

    @FlowValueInput("Player")
    private static PlayerValue.Reference player()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Gamemode")
    private static String gamemode()  {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next()  {
        throw new IllegalStateException();
    }
}
