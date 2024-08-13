package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

public record DiscardInstruction(Instruction value) implements Instruction {

    @Override
    public void prepare(NodeCompiler ctx) {
        ctx.prepare(value);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        InsnList list = new InsnList();
        list.add(ctx.compile(value, usedVars));
        list.add(new InsnNode(Opcodes.POP));
        return list;
    }

    @Override
    public Type returnType() {
        return Type.VOID_TYPE;
    }
}
