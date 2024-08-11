package de.blazemcworld.fireflow.node.impl.extraction.player;

import de.blazemcworld.fireflow.compiler.instruction.InstanceMethodInstruction;
import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.TextValue;
import org.objectweb.asm.Type;

import java.util.List;
import java.util.UUID;

public class PlayerUUIDNode extends ExtractionNode {
    public PlayerUUIDNode() {
        super("UUID", PlayerValue.INSTANCE, TextValue.INSTANCE);

        output.setInstruction(new MultiInstruction(
                Type.getType(String.class),
                new InstanceMethodInstruction(UUID.class,
                        new InstanceMethodInstruction(PlayerValue.Reference.class, input, "uuid", Type.getType(UUID.class), List.of()),
                        "toString", Type.getType(String.class), List.of()
                )
        ));
    }
}
