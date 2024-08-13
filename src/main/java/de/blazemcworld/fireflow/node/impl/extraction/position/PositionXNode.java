package de.blazemcworld.fireflow.node.impl.extraction.position;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PositionValue;
import net.minestom.server.coordinate.Pos;

public class PositionXNode extends ExtractionNode {
    public PositionXNode() {
        super("Position X", PositionValue.INSTANCE, NumberValue.INSTANCE);

        loadJava(PositionXNode.class);
    }

    @FlowValueOutput("")
    private static double output() {
        return input().x();
    }

    @FlowValueInput("")
    private static Pos input()  {
        throw new IllegalStateException();
    }
}
