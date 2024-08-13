package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public record RawInstruction(Type returnType, AbstractInsnNode... instructions) implements Instruction {

    @Override
    public void prepare(NodeCompiler ctx) {}

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        InsnList list = new InsnList();
        for (AbstractInsnNode insn : instructions) {
            list.add(insn);
        }
        return list;
    }

    @Override
    public Type returnType() {
        return returnType;
    }
}
