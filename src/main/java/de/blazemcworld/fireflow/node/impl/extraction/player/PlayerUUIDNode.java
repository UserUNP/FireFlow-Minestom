package de.blazemcworld.fireflow.node.impl.extraction.player;

import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.RawInstruction;
import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.TextValue;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.UUID;

public class PlayerUUIDNode extends ExtractionNode {
    public PlayerUUIDNode() {
        super("UUID", PlayerValue.INSTANCE, TextValue.INSTANCE);

        output.setInstruction(new MultiInstruction(
                Type.getType(String.class),
                PlayerValue.use(input, new MultiInstruction(Type.getType(String.class),
                        new RawInstruction(Type.getType(UUID.class), new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minestom/server/entity/Entity", "getUuid", "()Ljava/util/UUID;")),
                        new RawInstruction(Type.getType(String.class), new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/util/UUID", "toString", "()Ljava/lang/String;"))
                ),
                        new RawInstruction(Type.getType(String.class), new LdcInsnNode("00000000-0000-0000-0000-000000000000"))
                )
        ));
    }
}
