package de.blazemcworld.fireflow.node.impl.text;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.TextValue;

public class ConcatTextsNode extends Node {
    public ConcatTextsNode() {
        super("Concat Texts");

        input("Left", TextValue.INSTANCE);
        input("Right", TextValue.INSTANCE);
        output("Result", TextValue.INSTANCE);

        loadJava(ConcatTextsNode.class);
    }

    @FlowValueOutput("Result")
    private static String concat()  {
        return left() + right();
    }

    @FlowValueInput("Left")
    private static String left()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Right")
    private static String right()  {
        throw new IllegalStateException();
    }
}
