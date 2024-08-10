package de.blazemcworld.fireflow.node.impl.variable;

import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.compiler.instruction.GetInstanceFieldInstruction;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.space.Space;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Map;

public class PersistentVariableScope implements VariableScope {
    public static final PersistentVariableScope INSTANCE = new PersistentVariableScope();
    private PersistentVariableScope() {}

    @Override
    public String getName() {
        return "Persistent";
    }

    @Override
    public Instruction getStore() {
        return new GetInstanceFieldInstruction(Space.class,
                new GetInstanceFieldInstruction(CompiledNode.class,
                        new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                        "space", Type.getType(Space.class)
                ),
                "variables", Type.getType(Map.class)
        );
    }
}
