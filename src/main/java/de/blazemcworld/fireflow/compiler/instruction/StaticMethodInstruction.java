package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import it.unimi.dsi.fastutil.Pair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.ArrayList;
import java.util.List;

public record StaticMethodInstruction(Class<?> owner, String method, Type returnType, List<Pair<Type, Instruction>> arguments) implements Instruction {

    @Override
    public void prepare(NodeCompiler ctx) {
        for (Pair<Type, Instruction> arg : arguments) {
            ctx.prepare(arg.second());
        }
    }

    @Override
    public InsnList compile(NodeCompiler ctx) {
        InsnList out = new InsnList();
        List<Type> paramTypes = new ArrayList<>();
        for (Pair<Type, Instruction> arg : arguments) {
            paramTypes.add(arg.first());
            out.add(ctx.compile(arg.second()));
        }
        out.add(new MethodInsnNode(Opcodes.INVOKESTATIC, owner.getName().replace('.', '/'), method, Type.getMethodDescriptor(returnType, paramTypes.toArray(new Type[0])), owner.isInterface()));
        return out;
    }

    @Override
    public Type returnType() {
        return returnType;
    }
}
