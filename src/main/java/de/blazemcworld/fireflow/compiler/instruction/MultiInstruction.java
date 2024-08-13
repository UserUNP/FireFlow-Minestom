package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public record MultiInstruction(Type returnType, Instruction... instructions) implements Instruction {

    @Override
    public void prepare(NodeCompiler ctx) {
        for (Instruction i : instructions) {
            ctx.prepare(i);
        }
    }

    @Override
    public InsnList compile(NodeCompiler ctx, int usedVars) {
        InsnList out = new InsnList();
        for (Instruction i : instructions) {
            out.add(ctx.compile(i, usedVars));
        }
        return out;
    }

    @Override
    public Type returnType() {
        return returnType;
    }
}
