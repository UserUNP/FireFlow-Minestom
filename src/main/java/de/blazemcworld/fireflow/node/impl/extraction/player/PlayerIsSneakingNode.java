package de.blazemcworld.fireflow.node.impl.extraction.player;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import net.minestom.server.entity.Player;

public class PlayerIsSneakingNode extends ExtractionNode {

    public PlayerIsSneakingNode() {
        super("Player is Sneaking", PlayerValue.INSTANCE, ConditionValue.INSTANCE);

        loadJava(PlayerIsSneakingNode.class);
    }

    @FlowValueOutput("")
    private static boolean output() {
        Player player = input().resolve();

        return player != null && player.isSneaking();
    }

    @FlowValueInput("")
    private static PlayerValue.Reference input() {
        throw new IllegalStateException();
    }

}
