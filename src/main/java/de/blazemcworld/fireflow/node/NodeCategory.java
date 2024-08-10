package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.FireFlow;
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
import de.blazemcworld.fireflow.value.PlayerValue;
import de.blazemcworld.fireflow.value.TextValue;
import de.blazemcworld.fireflow.value.Value;
import it.unimi.dsi.fastutil.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class NodeCategory {

    public static final NodeCategory ROOT = new NodeCategory("Root", List.of(
            AddNumbersNode::new,
            PlayerJoinEvent::new,
            SendMessageNode::new,
            WhileNode::new
    ));

    public static final NodeCategory VARIABLES = new NodeCategory("Variables", ROOT, List.of(
            () -> new GetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE),
            () -> new SetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE)
    ));

    public static final Map<Value, NodeCategory> EXTRACTIONS = new HashMap<>();

    public static final NodeCategory PLAYER_EXTRACTIONS = new NodeCategory("Player Extractions", PlayerValue.INSTANCE, List.of(
            PlayerUUIDNode::new
    ));

    public static final NodeCategory TEXT_EXTRACTIONS = new NodeCategory("Text Extractions", TextValue.INSTANCE, List.of(
        TextToMessageNode::new
    ));


    public final String name;
    public final @Nullable NodeCategory parent;
    public final @Nullable Value extractionType;
    public final List<Pair<String, Supplier<Node>>> nodes = new ArrayList<>();
    public final List<NodeCategory> subcategories = new ArrayList<>();
    public NodeCategory(String name, @Nullable NodeCategory parent, @Nullable Value extractionType, List<Supplier<Node>> nodes) {
        this.name = name;
        this.parent = parent;
        this.extractionType = extractionType;

        for (Supplier<Node> s : nodes) {
            Node node = s.get();
            if (extractionType != null) {
                if (node instanceof ExtractionNode e) {
                    if (e.input.type != extractionType) {
                        FireFlow.LOGGER.warn("Node {} is not an extraction node of type {}, but present in category {}!", node.getBaseName(), extractionType, name);
                    }
                } else {
                    FireFlow.LOGGER.warn("Node {} is not an extraction node, but present in category {}!", node.getBaseName(), name);
                }
            }
            this.nodes.add(Pair.of(node.getBaseName(), s));
        }
        this.nodes.sort(Comparator.comparing(Pair::left));

        if (parent != null) {
            parent.subcategories.add(this);
            parent.subcategories.sort(Comparator.comparing(c -> c.name));
        }
        if (extractionType != null) {
            EXTRACTIONS.put(extractionType, this);
        }
    }

    public NodeCategory(String name, NodeCategory parent, List<Supplier<Node>> nodes) {
        this(name, parent, null, nodes);
    }

    public NodeCategory(String name, Value extractionType, List<Supplier<Node>> nodes) {
        this(name, null, extractionType, nodes);
    }

    public NodeCategory(String name, List<Supplier<Node>> nodes) {
        this(name, null, null, nodes);
    }
}
