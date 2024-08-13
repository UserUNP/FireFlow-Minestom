package de.blazemcworld.fireflow.node.impl.vector;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Vec;

public class ScaleVectorNode extends Node {

    public ScaleVectorNode() {
        super("Scale Vector");

        input("Vector", VectorValue.INSTANCE);
        input("Factor", NumberValue.INSTANCE);

        output("Vector", VectorValue.INSTANCE);

        loadJava(ScaleVectorNode.class);
    }

    @FlowValueOutput("Vector")
    private static Vec scale() {
        return vector().mul(factor());
    }

    @FlowValueInput("Vector")
    private static Vec vector() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Factor")
    private static double factor() {
        throw new IllegalStateException();
    }

}
