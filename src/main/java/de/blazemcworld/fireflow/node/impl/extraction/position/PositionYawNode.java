package de.blazemcworld.fireflow.node.impl.extraction.position;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PositionValue;
import net.minestom.server.coordinate.Pos;

public class PositionYawNode extends ExtractionNode {

    public PositionYawNode() {
        super("Position Yaw", PositionValue.INSTANCE, NumberValue.INSTANCE);
    }

    @FlowValueOutput("")
    private static double output() {
        return input().yaw();
    }

    @FlowValueInput("")
    private static Pos input()    {
        throw new IllegalStateException();
    }

}
