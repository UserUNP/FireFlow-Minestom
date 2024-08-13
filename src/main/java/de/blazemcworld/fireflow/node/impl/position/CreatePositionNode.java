package de.blazemcworld.fireflow.node.impl.position;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PositionValue;
import net.minestom.server.coordinate.Pos;

public class CreatePositionNode extends Node {

    public CreatePositionNode() {
        super("Create Position");

        input("X", NumberValue.INSTANCE);
        input("Y", NumberValue.INSTANCE);
        input("Z", NumberValue.INSTANCE);
        input("Pitch", NumberValue.INSTANCE);
        input("Yaw", NumberValue.INSTANCE);
        output("Position", PositionValue.INSTANCE);

        loadJava(CreatePositionNode.class);
    }

    @FlowValueOutput("Position")
    private static Pos pack() {
        return new Pos(x(), y(), z(), (float) pitch(), (float) yaw());
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

    @FlowValueInput("Pitch")
    private static double pitch() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Yaw")
    private static double yaw() {
        throw new IllegalStateException();
    }

}
