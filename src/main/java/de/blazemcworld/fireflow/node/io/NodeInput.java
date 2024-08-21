package de.blazemcworld.fireflow.node.io;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.Value;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class NodeInput implements NodeIO.In {
    private final String name;
    public final Value type;
    private NodeIO.Out source;
    private Object defaultValue;
    private Instruction instruction;

    public NodeInput(String name, Value type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Value getType() {
        return type;
    }

    @Override
    public boolean hasInset() {
        return source.hasInset();
    }

    @Override
    public NodeInputType inputType() {
        return NodeInputType.SINGULAR;
    }

    public void connectValue(NodeIO.Out source) {
        if (source.getType() != type) throw new IllegalStateException("Attempted to connect values of incompatible types!");
        this.source = source;
    }

    public void inset(Object value) {
        if (value == null) {
            source = null;
            return;
        }
        source = new NodeOutput("("+type.getFullName()+")", type);
        source.inset(value);
    }

    public Object getInset() {
        return source.getInset();
    }

    public void setInstruction(Instruction instructions) {
        if (type != SignalValue.INSTANCE) throw new IllegalStateException("Can only set instruction on signal inputs!");
        this.instruction = instructions;
    }

    @Override
    public void prepare(NodeCompiler ctx) {
        if (type == SignalValue.INSTANCE) {
            ctx.prepare(instruction);
            return;
        }
        if (source != null) {
            ctx.prepare(source);
        }
    }

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        if (type == SignalValue.INSTANCE) {
            return ctx.compile(instruction, usedVars);
        }
        if (source != null) {
            return ctx.compile(source, usedVars);
        }
        return type.compile(defaultValue);
    }

    @Override
    public Type returnType() {
        return type.getType();
    }

    public NodeInput withDefault(Object value) {
        defaultValue = value;
        return this;
    }

    public boolean hasDefault() {
        return defaultValue != null;
    }
}
