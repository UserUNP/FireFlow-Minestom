package de.blazemcworld.fireflow.node;

import de.blazemcworld.fireflow.node.impl.IfNode;
import de.blazemcworld.fireflow.node.impl.ScheduleNode;
import de.blazemcworld.fireflow.node.impl.ValuesEqualNode;
import de.blazemcworld.fireflow.node.impl.WhileNode;
import de.blazemcworld.fireflow.node.impl.event.PlayerInteractEventNode;
import de.blazemcworld.fireflow.node.impl.event.PlayerJoinEventNode;
import de.blazemcworld.fireflow.node.impl.extraction.number.NumberToTextNode;
import de.blazemcworld.fireflow.node.impl.extraction.player.PlayerNameNode;
import de.blazemcworld.fireflow.node.impl.extraction.player.PlayerUUIDNode;
import de.blazemcworld.fireflow.node.impl.extraction.text.FormatTextToMessageNode;
import de.blazemcworld.fireflow.node.impl.extraction.text.TextToMessageNode;
import de.blazemcworld.fireflow.node.impl.number.AddNumbersNode;
import de.blazemcworld.fireflow.node.impl.number.DivideNumbersNode;
import de.blazemcworld.fireflow.node.impl.number.MultiplyNumbersNode;
import de.blazemcworld.fireflow.node.impl.number.SubtractNumbersNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.GreaterEqualThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.GreaterThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.LessEqualThanNode;
import de.blazemcworld.fireflow.node.impl.number.comparison.LessThanNode;
import de.blazemcworld.fireflow.node.impl.player.*;
import de.blazemcworld.fireflow.node.impl.struct.UnpackStructNode;
import de.blazemcworld.fireflow.node.impl.text.ConcatTextsNode;
import de.blazemcworld.fireflow.node.impl.variable.*;
import de.blazemcworld.fireflow.value.NumberValue;
import de.blazemcworld.fireflow.value.StructValue;

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
                () -> new GetVariableNode(PersistentVariableScope.INSTANCE, NumberValue.INSTANCE),
                () -> new GetVariableNode(SpaceVariableScope.INSTANCE, NumberValue.INSTANCE),
                () -> new SetVariableNode(LocalVariableScope.INSTANCE, NumberValue.INSTANCE),
                () -> new SetVariableNode(PersistentVariableScope.INSTANCE, NumberValue.INSTANCE),
                () -> new SetVariableNode(SpaceVariableScope.INSTANCE, NumberValue.INSTANCE),
                () -> new ValuesEqualNode(NumberValue.INSTANCE),
                () -> new UnpackStructNode(StructValue.UNKNOWN),
                AddNumbersNode::new,
                ClearTitleNode::new,
                ConcatTextsNode::new,
                DivideNumbersNode::new,
                FormatTextToMessageNode::new,
                GreaterEqualThanNode::new,
                GreaterThanNode::new,
                IfNode::new,
                KillPlayerNode::new,
                LessEqualThanNode::new,
                LessThanNode::new,
                MultiplyNumbersNode::new,
                NumberToTextNode::new,
                PlayerInteractEventNode::new,
                PlayerJoinEventNode::new,
                PlayerNameNode::new,
                PlayerUUIDNode::new,
                ScheduleNode::new,
                SendActionBarNode::new,
                SendMessageNode::new,
                SendTitleNode::new,
                SetAllowPlayerFlyingNode::new,
                SetExperienceNode::new,
                SetLevelNode::new,
                SetPlayerFlyingNode::new,
                SetPlayerFoodNode::new,
                SetPlayerHealthNode::new,
                SetPlayerSaturationNode::new,
                SubtractNumbersNode::new,
                TextToMessageNode::new,
                WhileNode::new
        );

        for (Supplier<Node> each : all) {
            nodes.put(each.get().getBaseName(), each);
        }
    }

}
