package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class CpuCheckInstruction implements Instruction {
    @Override
    public void prepare(NodeCompiler ctx) {}

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        InsnList out = new InsnList();
        out.add(new VarInsnNode(Opcodes.ALOAD, 0));
        out.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "de/blazemcworld/fireflow/compiler/CompiledNode", "cpuCheck", "()V"));
        return out;
    }

    @Override
    public Type returnType() {
        return Type.VOID_TYPE;
    }
}
