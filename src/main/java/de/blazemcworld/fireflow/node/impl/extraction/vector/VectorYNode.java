package de.blazemcworld.fireflow.node.impl.extraction.vector;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Vec;

public class VectorYNode extends ExtractionNode {

    public VectorYNode() {
        super("Vector Y", VectorValue.INSTANCE, NumberValue.INSTANCE);

        loadJava(VectorYNode.class);
    }

    @FlowValueOutput("")
    public static double output() {
        return input().y();
    }

    @FlowValueInput("")
    private static Vec input()   {
        throw new IllegalStateException();
    }

}
