package de.blazemcworld.fireflow.node.impl.extraction.player;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.minestom.server.entity.Player;

public class PlayerNameNode extends ExtractionNode {

    public PlayerNameNode() {
        super("Player Name", PlayerValue.INSTANCE, TextValue.INSTANCE);

        loadJava(PlayerNameNode.class);
    }

    @FlowValueOutput("")
    private static String output(){
        Player player = input().resolve();
        String name = "";
        if (player != null) name = player.getUsername();
        return name;
    }

    @FlowValueInput("")
    private static PlayerValue.Reference input() {
        throw new IllegalStateException();
    }
}
