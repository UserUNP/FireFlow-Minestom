package de.blazemcworld.fireflow.node.impl.extraction.vector;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Vec;

public class VectorLengthNode extends ExtractionNode {

    public VectorLengthNode() {
        super("Vector Length", VectorValue.INSTANCE, NumberValue.INSTANCE);

        loadJava(VectorLengthNode.class);
    }

    @FlowValueOutput("")
    private static double output() {
        return input().length();
    }

    @FlowValueInput("")
    private static Vec input()  {
        throw new IllegalStateException();
    }

}
