package de.blazemcworld.fireflow.node.impl.extraction.text;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.kyori.adventure.text.Component;

public class FormatTextToMessageNode extends ExtractionNode {
    public FormatTextToMessageNode() {
        super("Format Text to Message", TextValue.INSTANCE, MessageValue.INSTANCE);

        loadJava(FormatTextToMessageNode.class);
    }

    @FlowValueOutput("")
    private static Component output() {
        return MessageValue.MM.deserialize(input());
    }

    @FlowValueInput("")
    private static String input() {
        throw new IllegalStateException();
    }
}
