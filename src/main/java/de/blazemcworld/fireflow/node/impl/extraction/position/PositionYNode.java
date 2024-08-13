package de.blazemcworld.fireflow.node.impl.extraction.position;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PositionValue;
import net.minestom.server.coordinate.Pos;

public class PositionYNode extends ExtractionNode {
    public PositionYNode() {
        super("Position Y", PositionValue.INSTANCE, NumberValue.INSTANCE);

        loadJava(PositionYNode.class);
    }

    @FlowValueOutput("")
    private static double output() {
        return input().y();
    }

    @FlowValueInput("")
    private static Pos input()  {
        throw new IllegalStateException();
    }
}
