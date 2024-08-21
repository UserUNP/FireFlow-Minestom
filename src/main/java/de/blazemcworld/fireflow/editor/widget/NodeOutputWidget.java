package de.blazemcworld.fireflow.editor.widget;

import de.blazemcworld.fireflow.compiler.FunctionDefinition;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.node.io.NodeIO;
import de.blazemcworld.fireflow.node.io.NodeOutput;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.value.SignalValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeOutputWidget extends ButtonWidget {

    public final NodeIO.Out output;
    public final NodeWidget parent;
    public Set<NodeInputWidget> connected = new HashSet<>();

    public NodeOutputWidget(Vec position, InstanceContainer inst, Component text, NodeIO.Out output, NodeWidget parent) {
        super(position, inst, text);
        this.output = output;
        this.parent = parent;
    }

    public void disconnect() {
        for (NodeInputWidget each : connected) {
            each.wires.removeIf(widget -> {
                if (widget.output != this) return false;
                if (output.getType() != SignalValue.INSTANCE) widget.input.input.inset(null);
                widget.remove();
                return true;
            });
        }
        connected.clear();
        if (output.getType() == SignalValue.INSTANCE) output.connectSignal(null);
    }

    @Override
    public void chat(Vec cursor, PlayerChatEvent event, CodeEditor editor) {
        if (parent.node instanceof FunctionDefinition.DefinitionNode def) {
            event.setCancelled(true);
            if (editor.inUse(def.getDefinition())) {
                event.getPlayer().sendMessage(Messages.error("Can't rename inputs of used functions!"));
                return;
            }
            FunctionDefinition prev = def.getDefinition();
            List<NodeOutput> updated = new ArrayList<>(prev.fnInputs);
            int id = updated.indexOf(output);
            if (id == -1) return;
            updated.set(id, new NodeOutput(event.getMessage(), output.getType()));
            FunctionDefinition next = new FunctionDefinition(prev.fnName, updated, prev.fnOutputs);
            editor.redefine(prev, next);
        }
    }

    @Override
    public void leftClick(Vec cursor, Player player, CodeEditor editor) {
        if (parent.node instanceof FunctionDefinition.DefinitionNode def) {
            if (editor.inUse(def.getDefinition())) {
                player.sendMessage(Messages.error("Can't delete inputs of used functions!"));
                return;
            }
            FunctionDefinition prev = def.getDefinition();
            List<NodeOutput> updated = new ArrayList<>(prev.fnInputs);
            updated.remove(output);
            FunctionDefinition next = new FunctionDefinition(prev.fnName, updated, prev.fnOutputs);
            editor.redefine(prev, next);
            return;
        }
        super.leftClick(cursor, player, editor);
    }

    @Override
    public void update() {
        super.update();
        if (this.connected == null) return;
        for (NodeInputWidget each : connected) {
            for (WireWidget wire : each.wires) {
                if (wire.output == this) wire.update();
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
        disconnect();
    }
}
