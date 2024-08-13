package de.blazemcworld.fireflow.node.impl.extraction.player;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.PlayerValue;
import net.minestom.server.entity.Player;

public class PlayerIsOnGroundNode extends ExtractionNode {

    public PlayerIsOnGroundNode() {
        super("Player Is On Ground", PlayerValue.INSTANCE, ConditionValue.INSTANCE);

        loadJava(PlayerIsOnGroundNode.class);
    }

    @FlowValueOutput("")
    private static boolean output() {
        Player player = input().resolve();

        return player != null && player.isOnGround();
    }

    @FlowValueInput("")
    private static PlayerValue.Reference input() {
        throw new IllegalStateException();
    }

}
