package de.blazemcworld.fireflow.node.impl.number;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.NumberValue;

public class RandomNumberNode extends Node {

    public RandomNumberNode() {
        super("Random Number");

        input("Minimum", NumberValue.INSTANCE).withDefault(0);
        input("Maximum", NumberValue.INSTANCE).withDefault(1);
        input("Whole", ConditionValue.INSTANCE).withDefault(false);
        output("Result", NumberValue.INSTANCE);

        loadJava(RandomNumberNode.class);
    }

    @FlowValueOutput("Result")
    private static double random() {
        double min = minimum();
        double max = maximum();
        double result;

        if (whole()) {
            result = Math.floor(Math.random() * (max - min + 1) + min);
        } else {
            result = Math.random() * (max - min) + min;
        }

        return result;
    }

    @FlowValueInput("Minimum")
    private static double minimum() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Maximum")
    private static double maximum()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Whole")
    private static boolean whole()   {
        throw new IllegalStateException();
    }

}
