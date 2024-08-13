package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import it.unimi.dsi.fastutil.Pair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;
import java.util.List;

public record InstanceMethodInstruction(Class<?> owner, Instruction target, String method, Type returnType, List<Pair<Type, Instruction>> arguments) implements Instruction {

    @Override
    public void prepare(NodeCompiler ctx) {
        ctx.prepare(target);
        for (Pair<Type, Instruction> arg : arguments) {
            ctx.prepare(arg.second());
        }
    }

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        InsnList out = new InsnList();
        out.add(ctx.compile(target, usedVars));
        List<Type> paramTypes = new ArrayList<>();
        for (Pair<Type, Instruction> arg : arguments) {
            paramTypes.add(arg.first());
            out.add(ctx.compile(arg.second(), usedVars));
        }
        out.add(new MethodInsnNode(owner.isInterface() ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL, owner.getName().replace('.', '/'), method, Type.getMethodDescriptor(returnType, paramTypes.toArray(new Type[0]))));
        return out;
    }

    @Override
    public Type returnType() {
        return returnType;
    }
}
