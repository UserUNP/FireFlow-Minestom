package de.blazemcworld.fireflow.node.impl.extraction.position;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.PositionValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class PositionFacingDirectionNode extends ExtractionNode {

    public PositionFacingDirectionNode() {
        super("Position Facing Direction", PositionValue.INSTANCE, VectorValue.INSTANCE);

        loadJava(PositionFacingDirectionNode.class);
    }

    @FlowValueOutput("")
    private static Vec output() {
        return input().direction();
    }

    @FlowValueInput("")
    private static Pos input() {
        throw new IllegalStateException();
    }

}
