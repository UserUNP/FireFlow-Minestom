package de.blazemcworld.fireflow.editor.widget;

import com.google.gson.JsonParser;
import de.blazemcworld.fireflow.compiler.FunctionDefinition;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.editor.Widget;
import de.blazemcworld.fireflow.node.io.NodeIO;
import de.blazemcworld.fireflow.node.io.NodeInput;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.value.SignalValue;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;

public class NodeInputWidget extends ButtonWidget {

    public final NodeIO.In input;
    public final NodeWidget parent;
    public List<WireWidget> wires = new ArrayList<>();

    public NodeInputWidget(Vec position, InstanceContainer inst, Component text, NodeIO.In input, NodeWidget parent) {
        super(position, inst, text);
        this.input = input;
        this.parent = parent;
    }

    @Override
    public Widget select(Player player, Vec cursor) {
        for (WireWidget wire : wires) {
            Widget out = wire.select(player, cursor);
            if (out != null) return out;
        }
        return super.select(player, cursor);
    }

    public void addWire(WireWidget wire) {
        wires.add(wire);
        wire.output.connected.add(this);
        update();
        wire.output.update();
    }

    @Override
    public void update() {
        if (input != null) {
            Component text;
            if (input.getInset() != null) {
                text = Component.text("⏹ " + input.getType().formatInset(input.getInset())).color(input.getType().getColor());
            } else {
                text = Component.text("○ " + input.getName()).color(input.getType().getColor());
            }
            if (input.hasDefault()) text = text.append(Component.text("*").color(NamedTextColor.GRAY));
            text(text);
        }

        super.update();
        if (wires == null) return;
        for (WireWidget wire : wires) wire.update();
    }

    @Override
    public void chat(Vec cursor, PlayerChatEvent event, CodeEditor editor) {
        event.setCancelled(true);
        if (parent.node instanceof FunctionDefinition.DefinitionNode def) {
            if (editor.inUse(def.getDefinition())) {
                event.getPlayer().sendMessage(Messages.error("Can't rename outputs of used functions!"));
                return;
            }
            FunctionDefinition prev = def.getDefinition();
            List<NodeInput> updated = new ArrayList<>(prev.fnOutputs);
            int id = updated.indexOf(input);
            if (id == -1) return;
            updated.set(id, new NodeInput(event.getMessage(), input.getType()));
            FunctionDefinition next = new FunctionDefinition(prev.fnName, prev.fnInputs, updated);
            editor.redefine(prev, next);
            return;
        }
        String str;
        try {
            str = JsonParser.parseString(event.getMessage()).getAsString();
        } catch (Exception ignored) {
            str = event.getMessage();
        }
        Object inset = input.getType().prepareInset(str);
        if (inset != null) {
            input.inset(inset);
            parent.update(false);
        } else {
            event.getPlayer().sendMessage(Messages.error("Failed to inset input value!"));
        }
    }

    @Override
    public void leftClick(Vec cursor, Player player, CodeEditor editor) {
        if (parent.node instanceof FunctionDefinition.DefinitionNode def) {
            if (editor.inUse(def.getDefinition())) {
                player.sendMessage(Messages.error("Can't delete outputs of used functions!"));
                return;
            }
            FunctionDefinition prev = def.getDefinition();
            List<NodeInput> updated = new ArrayList<>(prev.fnOutputs);
            updated.remove(input);
            FunctionDefinition next = new FunctionDefinition(prev.fnName, prev.fnInputs, updated);
            editor.redefine(prev, next);
            return;
        }
        super.leftClick(cursor, player, editor);
    }

    public void disconnect() {
        for (WireWidget wire : wires) {
            wire.remove();
            wire.output.connected.remove(this);
            if (input.getType() == SignalValue.INSTANCE) wire.output.output.connectSignal(null);
        }
        if (input.getType() != SignalValue.INSTANCE) input.inset(null);
        wires.clear();
    }

    @Override
    public void remove() {
        super.remove();
        disconnect();
    }
}
