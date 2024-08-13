package de.blazemcworld.fireflow.node.impl.vector;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.PositionValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class VectorToPositionNode extends Node {

    public VectorToPositionNode() {
        super("Vector to Position");

        input("Vector", VectorValue.INSTANCE);
        input("Pitch", NumberValue.INSTANCE);
        input("Yaw", NumberValue.INSTANCE);
        output("Position", PositionValue.INSTANCE);

        loadJava(VectorToPositionNode.class);
    }

    @FlowValueOutput("Position")
    private static Pos convert() {
        Vec v = vector();
        return new Pos(v.x(), v.y(), v.z(), (float) pitch(), (float) yaw());
    }

    @FlowValueInput("Vector")
    private static Vec vector() {
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
