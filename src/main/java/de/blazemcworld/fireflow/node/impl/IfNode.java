package de.blazemcworld.fireflow.node.impl;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.SignalValue;

public class IfNode extends Node {

    public IfNode() {
        super("If");

        input("Signal", SignalValue.INSTANCE);
        input("Case", ConditionValue.INSTANCE);
        output("True", SignalValue.INSTANCE);
        output("False", SignalValue.INSTANCE);

        loadJava(IfNode.class);
    }

    @FlowSignalInput("Signal")
    private static void signal() {
        if (caseBool()) trueSignal();
        else falseSignal();
    }

    @FlowValueInput("Case")
    private static boolean caseBool() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("True")
    private static void trueSignal() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("False")
    private static void falseSignal() {
        throw new IllegalStateException();
    }
}
