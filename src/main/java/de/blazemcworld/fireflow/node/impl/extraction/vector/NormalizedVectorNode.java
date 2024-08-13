package de.blazemcworld.fireflow.node.impl.extraction.vector;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Vec;

public class NormalizedVectorNode extends ExtractionNode {

    public NormalizedVectorNode() {
        super("Normalized Vector", VectorValue.INSTANCE, VectorValue.INSTANCE);

        loadJava(NormalizedVectorNode.class);
    }

    @FlowValueOutput("")
    private static Vec normalize() {
        return vector().normalize();
    }

    @FlowValueInput("")
    private static Vec vector()  {
        throw new IllegalStateException();
    }

}
