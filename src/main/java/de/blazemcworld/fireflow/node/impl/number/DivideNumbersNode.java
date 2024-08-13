package de.blazemcworld.fireflow.node.impl.number;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;

public class DivideNumbersNode extends Node {
    public DivideNumbersNode() {
        super("Divide Numbers");

        input("Left", NumberValue.INSTANCE);
        input("Right", NumberValue.INSTANCE);
        output("Result", NumberValue.INSTANCE);

        loadJava(DivideNumbersNode.class);
    }

    @FlowValueOutput("Result")
    private static double divide()  {
        return left() / right();
    }

    @FlowValueInput("Left")
    private static double left()   {
        throw new IllegalStateException();
    }

    @FlowValueInput("Right")
    private static double right()  {
        throw new IllegalStateException();
    }
}
