package de.blazemcworld.fireflow.editor.widget;

import de.blazemcworld.fireflow.node.NodeOutput;
import de.blazemcworld.fireflow.value.SignalValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.InstanceContainer;

import java.util.HashSet;
import java.util.Set;

public class NodeOutputWidget extends ButtonWidget {

    public final NodeOutput output;
    public final NodeWidget parent;
    public Set<NodeInputWidget> connected = new HashSet<>();

    public NodeOutputWidget(Vec position, InstanceContainer inst, Component text, NodeOutput output, NodeWidget parent) {
        super(position, inst, text);
        this.output = output;
        this.parent = parent;
    }

    public void disconnect() {
        for (NodeInputWidget each : connected) {
            each.wires.removeIf(widget -> {
                if (widget.output != this) return false;
                if (output.type != SignalValue.INSTANCE) widget.input.input.inset(null);
                widget.remove();
                return true;
            });
        }
        connected.clear();
        if (output.type == SignalValue.INSTANCE) output.connectSignal(null);
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
