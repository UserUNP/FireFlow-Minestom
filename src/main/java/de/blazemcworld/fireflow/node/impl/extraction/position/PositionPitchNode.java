package de.blazemcworld.fireflow.node.impl.extraction.position;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PositionValue;
import net.minestom.server.coordinate.Pos;

public class PositionPitchNode extends ExtractionNode {
    public PositionPitchNode() {
        super("Position Pitch", PositionValue.INSTANCE, NumberValue.INSTANCE);

        loadJava(PositionPitchNode.class);
    }

    @FlowValueOutput("")
    private static double output() {
        return input().pitch();
    }

    @FlowValueInput("")
    private static Pos input()   {
        throw new IllegalStateException();
    }
}
