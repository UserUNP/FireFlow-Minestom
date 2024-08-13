package de.blazemcworld.fireflow.node.impl.vector;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Vec;

public class CreateVectorNode extends Node {

    public CreateVectorNode() {
        super("Create Vector");

        input("X", NumberValue.INSTANCE);
        input("Y", NumberValue.INSTANCE);
        input("Z", NumberValue.INSTANCE);
        output("Vector", VectorValue.INSTANCE);

        loadJava(CreateVectorNode.class);
    }

    @FlowValueOutput("Vector")
    private static Vec pack() {
        return new Vec(x(), y(), z());
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
