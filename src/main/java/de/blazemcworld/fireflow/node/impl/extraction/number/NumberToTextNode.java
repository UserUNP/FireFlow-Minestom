package de.blazemcworld.fireflow.node.impl.extraction.number;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.TextValue;

public class NumberToTextNode extends ExtractionNode {

    public NumberToTextNode() {
        super("Number to Text", NumberValue.INSTANCE, TextValue.INSTANCE);

        loadJava(NumberToTextNode.class);
    }

    @FlowValueOutput("")
    private static String output() {
        return Double.toString(input());
    }

    @FlowValueInput("")
    private static double input()  {
        throw new IllegalStateException();
    }

}
