package de.blazemcworld.fireflow.compiler.instruction;

import de.blazemcworld.fireflow.compiler.NodeCompiler;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnList;

public interface Instruction {
    void prepare(NodeCompiler ctx);
    InsnList compile(NodeCompiler ctx, int usedVars);
    Type returnType();
}
