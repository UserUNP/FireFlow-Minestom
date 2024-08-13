package de.blazemcworld.fireflow.node.impl.number.comparison;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.NumberValue;

public class GreaterThanNode extends Node {

    public GreaterThanNode() {
        super("Greater than");

        input("Left", NumberValue.INSTANCE);
        input("Right", NumberValue.INSTANCE);
        output("Result", ConditionValue.INSTANCE);

        loadJava(GreaterThanNode.class);
    }

    @FlowValueOutput("Result")
    private static boolean result() {
        return left() > right();
    }

    @FlowValueInput("Left")
    private static double left() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Right")
    private static double right() {
        throw new IllegalStateException();
    }

}
