package de.blazemcworld.fireflow.node.impl.extraction.text;

import de.blazemcworld.fireflow.compiler.instruction.MultiInstruction;
import de.blazemcworld.fireflow.compiler.instruction.StaticMethodInstruction;
import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.TextValue;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.objectweb.asm.Type;

import java.util.List;

public class TextToMessageNode extends ExtractionNode {
    public TextToMessageNode() {
        super("To Message", TextValue.INSTANCE, MessageValue.INSTANCE);

        output.setInstruction(new MultiInstruction(
                Type.getType(Component.class),
                new StaticMethodInstruction(
                        Component.class,
                        "text",
                        Type.getType(TextComponent.class),
                        List.of(Pair.of(Type.getType(String.class), input))
                )
        ));
    }
}
