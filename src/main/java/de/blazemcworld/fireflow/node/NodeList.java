package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.node.impl.AddNumbersNode;
import de.blazemcworld.fireflow.node.impl.SendMessageNode;
import de.blazemcworld.fireflow.node.impl.WhileNode;
import de.blazemcworld.fireflow.node.impl.event.PlayerJoinEvent;
import de.blazemcworld.fireflow.node.impl.extraction.player.PlayerUUIDNode;
import de.blazemcworld.fireflow.node.impl.extraction.text.TextToMessageNode;
import de.blazemcworld.fireflow.node.impl.variable.GetVariableNode;
import de.blazemcworld.fireflow.node.impl.variable.LocalVariableScope;
import de.blazemcworld.fireflow.node.impl.variable.SetVariableNode;
import de.blazemcworld.fireflow.value.NumberValue;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class NodeList {

    public static final HashMap<String, Supplier<Node>> nodes = new HashMap<>();

    static {
        List<Supplier<Node>> all = List.of(
                // Sorted alphabetically
                // TIP: Sort the lines automatically using your ide
                () -> new GetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE),
                () -> new SetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE),
                AddNumbersNode::new,
                PlayerJoinEvent::new,
                PlayerUUIDNode::new,
                TextToMessageNode::new,
                SendMessageNode::new,
                WhileNode::new
        );

        for (Supplier<Node> each : all) {
            nodes.put(each.get().getBaseName(), each);
        }
    }

}
