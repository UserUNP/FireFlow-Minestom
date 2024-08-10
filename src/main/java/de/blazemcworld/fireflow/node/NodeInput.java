package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.Value;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class NodeInput implements Instruction {
    private final String name;
    public final Value type;
    private NodeOutput source;
    private Object inset;
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

    public void connectValue(NodeOutput source) {
        if (source.getType() != type) throw new IllegalStateException("Attempted to connect values of incompatible types!");
        this.source = source;
        this.inset = null;
    }

    public void inset(Object value) {
        source = null;
        this.inset = value;
    }

    public Object getInset() {
        return inset;
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
    public InsnList compile(NodeCompiler ctx) {
        if (type == SignalValue.INSTANCE) {
            return ctx.compile(instruction);
        }
        if (source != null) {
            return ctx.compile(source);
        }
        return type.compile(ctx, inset);
    }

    @Override
    public Type returnType() {
        return type.getType();
    }
}
