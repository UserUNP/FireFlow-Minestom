package de.blazemcworld.fireflow.node.impl.position;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PositionValue;
import net.minestom.server.coordinate.Pos;

public class ShiftPositionXYZNode extends Node {

    public ShiftPositionXYZNode() {
        super("Shift Position XYZ");

        input("Position", PositionValue.INSTANCE);
        input("X", NumberValue.INSTANCE);
        input("Y", NumberValue.INSTANCE);
        input("Z", NumberValue.INSTANCE);
        output("Position", PositionValue.INSTANCE);

        loadJava(ShiftPositionXYZNode.class);
    }

    @FlowValueOutput("Position")
    private static Pos shift() {
        return pos().add(x(), y(), z());
    }

    @FlowValueInput("Position")
    private static Pos pos() {
        throw new IllegalStateException();
    }

    @FlowValueInput("X")
    private static double x() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Y")
    private static double y() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Z")
    private static double z() {
        throw new IllegalStateException();
    }

}
