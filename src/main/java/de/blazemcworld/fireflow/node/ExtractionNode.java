package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.value.Value;

public class ExtractionNode extends Node{

    public NodeInput input;
    public NodeOutput output;

    public ExtractionNode(String name, Value input, Value output) {
        super(name);
        this.input = input("", input);
        this.output = output("", output);
    }
}
