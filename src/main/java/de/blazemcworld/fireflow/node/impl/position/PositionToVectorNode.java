package de.blazemcworld.fireflow.node.impl.position;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.PositionValue;
import de.blazemcworld.fireflow.value.VectorValue;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class PositionToVectorNode extends Node {

    public PositionToVectorNode() {
        super("Position to Vector");

        input("Position", PositionValue.INSTANCE);
        output("Vector", VectorValue.INSTANCE);

        loadJava(PositionToVectorNode.class);
    }

    @FlowValueOutput("Vector")
    private static Vec convert() {
        Pos p = position();
        return new Vec(p.x(), p.y(), p.z());
    }

    @FlowValueInput("Position")
    private static Pos position() {
        throw new IllegalStateException();
    }

}
