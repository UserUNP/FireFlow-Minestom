package de.blazemcworld.fireflow.node.impl.position;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.PositionValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class ShiftPositionVectorNode extends Node {

    public ShiftPositionVectorNode() {
        super("Shift Position Vector");

        input("Position", PositionValue.INSTANCE);
        input("Vector", PositionValue.INSTANCE);
        output("Position", PositionValue.INSTANCE);

        loadJava(ShiftPositionVectorNode.class);
    }

    @FlowValueOutput("Position")
    private static Pos shift() {
        return pos().add(vec());
    }

    @FlowValueInput("Position")
    private static Pos pos() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Vector")
    private static Vec vec() {
        throw new IllegalStateException();
    }

}
