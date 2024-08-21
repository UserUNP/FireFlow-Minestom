package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public record RawInstruction(Type returnType, InsnList list) implements Instruction {

    public RawInstruction(Type returnType, AbstractInsnNode... nodes) {
        this(returnType, toList(nodes));
    }

    private static InsnList toList(AbstractInsnNode[] nodes) {
        InsnList list = new InsnList();
        for (AbstractInsnNode n : nodes) list.add(n);
        return list;
    }

    @Override
    public void prepare(NodeCompiler ctx) {}

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        return list;
    }

    @Override
    public Type returnType() {
        return returnType;
    }
}
