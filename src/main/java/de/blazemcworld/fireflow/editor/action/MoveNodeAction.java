package de.blazemcworld.fireflow.editor.action;

import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.editor.EditorAction;
import de.blazemcworld.fireflow.editor.widget.NodeWidget;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;

public class MoveNodeAction implements EditorAction {
    private final Vec offset;
    private final NodeWidget node;
    private final Player player;
    private final CodeEditor editor;

    public MoveNodeAction(Vec offset, NodeWidget node, Player player, CodeEditor editor) {
        this.offset = offset;
        this.node = node;
        this.player = player;
        this.editor = editor;
    }

    @Override
    public void tick(Vec cursor) {
        node.origin = cursor.add(offset);
        node.update(false);
    }

    @Override
    public void rightClick(Vec cursor) {
        editor.setAction(player, null);
    }

    @Override
    public void stop() {
        node.border.color(NamedTextColor.WHITE);
        node.update(false);
    }
}
