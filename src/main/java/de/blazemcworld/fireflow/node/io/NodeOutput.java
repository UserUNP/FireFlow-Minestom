package de.blazemcworld.fireflow.node.io;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.Value;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class NodeOutput implements NodeIO.Out {
    private final String name;
    public final Value type;
    private NodeIO.In target;
    private Instruction instruction = null;
    private Object inset;

    public NodeOutput(String name, Value type) {
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
        return inset != null;
    }

    public void setInstruction(Instruction instructions) {
        if (type == SignalValue.INSTANCE) throw new IllegalStateException("Can't set instruction on signal output!");
        this.instruction = instructions;
        inset = null;
    }

    public void inset(@Nullable Object inset) {
        if (type == SignalValue.INSTANCE) throw new IllegalStateException("Can't set inset on signal output!");
        this.inset = inset;
        instruction = null;
    }

    public Object getInset() {
        return inset;
    }

    @Override
    public void prepare(NodeCompiler ctx) {
        if (type == SignalValue.INSTANCE) {
            if (target == null) return;
            ctx.prepare(target);
            return;
        }
        if (instruction != null) {
            ctx.prepare(instruction);
        }
    }

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        if (type == SignalValue.INSTANCE) {
            if (target == null) return new InsnList();
            return ctx.compile(target, usedVars);
        }
        if (instruction != null) {
            return ctx.compile(instruction, usedVars);
        }
        if (inset != null) {
            return ctx.compile(new RawInstruction(type.getType(), type.compile(inset)), usedVars);
        }
        throw new IllegalStateException("Missing instructions on value output!");
    }

    @Override
    public Type returnType() {
        return type.getType();
    }

    public void connectSignal(NodeIO.In target) {
        if (type != SignalValue.INSTANCE) throw new IllegalStateException("Attempted to connect non signal value!");
        if (target == null) {
            this.target = null;
            return;
        }
        if (target.getType() != type) throw new IllegalStateException("Attempted to connect values of incompatible types!");
        this.target = target;
    }
}
