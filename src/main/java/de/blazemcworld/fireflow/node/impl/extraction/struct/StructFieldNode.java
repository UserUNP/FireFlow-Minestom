package de.blazemcworld.fireflow.node.impl.extraction.struct;

import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.value.StructValue;
import de.blazemcworld.fireflow.value.Value;
import it.unimi.dsi.fastutil.Pair;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class StructFieldNode extends ExtractionNode {
    public StructFieldNode(StructValue type, int i, Pair<String, Value> pair) {
        super(pair.left(), type, pair.right());
        Value fieldType = pair.right();
        output.setInstruction(new MultiInstruction(fieldType.getType(),
                input,
                new RawInstruction(Type.VOID_TYPE, new LdcInsnNode(i)),
                fieldType.cast(new RawInstruction(Type.VOID_TYPE, new InsnNode(Opcodes.AALOAD)))
        ));
    }
}
