package de.blazemcworld.fireflow.node.impl.extraction.player;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.PlayerValue;

public class PlayerIsPlayingNode extends ExtractionNode {

    public PlayerIsPlayingNode() {
        super("Player is Playing", PlayerValue.INSTANCE, ConditionValue.INSTANCE);

        loadJava(PlayerIsPlayingNode.class);
    }

    @FlowValueOutput("")
    private static boolean output() {
        return input().resolve() != null;
    }

    @FlowValueInput("")
    private static PlayerValue.Reference input() {
        throw new IllegalStateException();
    }

}
