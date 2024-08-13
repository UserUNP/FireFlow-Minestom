package de.blazemcworld.fireflow.node.impl;

import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowContext;
import de.blazemcworld.fireflow.node.annotation.FlowSignalInput;
import de.blazemcworld.fireflow.node.annotation.FlowSignalOutput;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.value.ConditionValue;
import de.blazemcworld.fireflow.value.SignalValue;

public class WhileNode extends Node {
    public WhileNode() {
        super("While Loop");

        input("Signal", SignalValue.INSTANCE);
        input("Condition", ConditionValue.INSTANCE);
        output("Loop", SignalValue.INSTANCE);
        output("Next", SignalValue.INSTANCE);

        loadJava(WhileNode.class);
    }

    @FlowSignalInput("Signal")
    private static void signal() {
        while (condition()) {
            ctx().cpuCheck();
            loop();
        }
        next();
    }

    @FlowSignalOutput("Loop")
    private static void loop() {
        throw new IllegalStateException();
    }

    @FlowSignalOutput("Next")
    private static void next() {
        throw new IllegalStateException();
    }

    @FlowValueInput("Condition")
    private static boolean condition() {
        throw new IllegalStateException();
    }

    @FlowContext
    private static CompiledNode ctx() {
        throw new IllegalStateException();
    }
}
