package de.blazemcworld.fireflow.node.impl.extraction.player;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.PositionValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class PlayerPositionNode extends ExtractionNode {
    public PlayerPositionNode() {
        super("Player Position", PlayerValue.INSTANCE, PositionValue.INSTANCE);

        loadJava(PlayerPositionNode.class);
    }

    @FlowValueOutput("")
    private static Pos output() {
        Player p = input().resolve();
        return p == null ? Pos.ZERO : p.getPosition();
    }

    @FlowValueInput("")
    private static PlayerValue.Reference input() {
        throw new IllegalStateException();
    }
}
