package de.blazemcworld.fireflow.node.impl.extraction.vector;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Vec;

public class VectorZNode extends ExtractionNode {

    public VectorZNode() {
        super("Vector Z", VectorValue.INSTANCE, NumberValue.INSTANCE);

        loadJava(VectorZNode.class);
    }

    @FlowValueOutput("")
    private static double output() {
        return input().z();
    }

    @FlowValueInput("")
    private static Vec input()  {
        throw new IllegalStateException();
    }

}
