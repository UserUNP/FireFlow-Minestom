package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.evaluation.CodeEvaluator;
import de.blazemcworld.fireflow.value.AllValues;
import de.blazemcworld.fireflow.value.Value;
import net.minestom.server.network.NetworkBuffer;

import java.util.ArrayList;
import java.util.List;

public abstract class Node {

    public List<NodeInput> inputs = new ArrayList<>();
    public List<NodeOutput> outputs = new ArrayList<>();
    public final String name;

    public Node(String name) {
        this.name = name;
    }

    protected NodeInput input(String name, Value value) {
        NodeInput input = new NodeInput(name, value);
        inputs.add(input);
        return input;
    }

    protected NodeOutput output(String name, Value value) {
        NodeOutput output = new NodeOutput(name, value);
        outputs.add(output);
        return output;
    }

    public void register(CodeEvaluator evaluator) {}

    protected static long id = 0;
    public static String allocateId() {
        return Long.toHexString(id++);
    }

    public String getBaseName() {
        return name;
    }

    public void writeData(NetworkBuffer buffer) {
        List<Value> generics = generics();
        buffer.write(NetworkBuffer.INT, generics.size());
        for (Value generic : generics) {
            writeValue(buffer, generic);
        }

        List<NodeInput> insetted = new ArrayList<>();
        for (NodeInput input : inputs) {
            if (input.getInset() != null) insetted.add(input);
        }
        buffer.write(NetworkBuffer.INT, insetted.size());
        for (NodeInput input : insetted) {
            buffer.write(NetworkBuffer.INT, inputs.indexOf(input));
            input.type.writeInset(buffer, input.getInset());
        }
    }

    private void writeValue(NetworkBuffer buffer, Value value) {
        buffer.write(NetworkBuffer.STRING, value.getBaseName());
        List<Value> generics = value.toGenerics();
        buffer.write(NetworkBuffer.INT, generics.size());
        for (Value generic : generics) {
            writeValue(buffer, generic);
        }
    }

    public Node readData(NetworkBuffer buffer) {
        int genericsSize = buffer.read(NetworkBuffer.INT);
        List<Value> generics = new ArrayList<>();
        for (int i = 0; i < genericsSize; i++) {
            generics.add(readValue(buffer));
        }

        Node target = fromGenerics(generics);

        int size = buffer.read(NetworkBuffer.INT);
        for (int i = 0; i < size; i++) {
            int index = buffer.read(NetworkBuffer.INT);
            target.inputs.get(index).inset(target.inputs.get(index).type.readInset(buffer));
        }

        return target;
    }

    private Value readValue(NetworkBuffer buffer) {
        String name = buffer.read(NetworkBuffer.STRING);
        List<Value> generics = new ArrayList<>();
        int genericsSize = buffer.read(NetworkBuffer.INT);
        for (int i = 0; i < genericsSize; i++) {
            generics.add(readValue(buffer));
        }
        Value norm = AllValues.get(name);
        if (norm == null) return null;
        return norm.fromGenerics(generics);
    }

    public List<Value> generics() {
        return List.of();
    }

    public List<List<Value>> possibleGenerics() {
        return List.of();
    }

    public Node fromGenerics(List<Value> generics) {
        return this;
    }
}
