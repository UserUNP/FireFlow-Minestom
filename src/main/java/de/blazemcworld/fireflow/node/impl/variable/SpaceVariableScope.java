package de.blazemcworld.fireflow.node.impl.variable;

import de.blazemcworld.fireflow.compiler.CompiledNode;
import de.blazemcworld.fireflow.compiler.instruction.GetInstanceFieldInstruction;
import de.blazemcworld.fireflow.compiler.instruction.Instruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.evaluation.CodeEvaluator;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.Map;

public class SpaceVariableScope implements VariableScope {
    public static final SpaceVariableScope INSTANCE = new SpaceVariableScope();

    private SpaceVariableScope() {
    }

    @Override
    public String getName() {
        return "Space";
    }

    @Override
    public Instruction getStore() {
        return new GetInstanceFieldInstruction(CodeEvaluator.class,
                new GetInstanceFieldInstruction(CompiledNode.class,
                        new RawInstruction(Type.getType(CompiledNode.class), new VarInsnNode(Opcodes.ALOAD, 0)),
                        "evaluator", Type.getType(CodeEvaluator.class)
                ),
                "variables", Type.getType(Map.class)
        );
    }
}
