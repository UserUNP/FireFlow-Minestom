package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;

public record GetInstanceFieldInstruction(Class<?> owner, Instruction target, String name, Type type) implements Instruction {
    @Override
    public void prepare(NodeCompiler ctx) {
        ctx.prepare(target);
    }

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        InsnList out = new InsnList();
        out.add(ctx.compile(target, usedVars));
        out.add(new FieldInsnNode(Opcodes.GETFIELD, owner.getName().replace('.', '/'), name, type.getDescriptor()));
        return out;
    }

    @Override
    public Type returnType() {
        return type;
    }
}
