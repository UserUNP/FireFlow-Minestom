package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.value.SignalValue;
import de.blazemcworld.fireflow.value.Value;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public class NodeOutput implements Instruction {
    private final String name;
    public final Value type;
    public NodeInput target;
    private Instruction instruction = null;
    public final String id = Node.allocateId();

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

    public void setInstruction(Instruction instructions) {
        if (type == SignalValue.INSTANCE) throw new IllegalStateException("Can't set instruction on signal output!");
        this.instruction = instructions;
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
    public InsnList compile(NodeCompiler ctx) {
        if (type == SignalValue.INSTANCE) {
            if (target == null) return new InsnList();
            return ctx.compile(target);
        }
        if (instruction != null) {
            return ctx.compile(instruction);
        }
        throw new IllegalStateException("Missing instructions on value output!");
    }

    @Override
    public Type returnType() {
        return type.getType();
    }

    public void connectSignal(NodeInput target) {
        if (type != SignalValue.INSTANCE) throw new IllegalStateException("Attempted to connect non signal value!");
        if (target == null) {
            this.target = null;
            return;
        }
        if (target.getType() != type) throw new IllegalStateException("Attempted to connect values of incompatible types!");
        this.target = target;
    }
}
