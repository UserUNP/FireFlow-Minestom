package de.blazemcworld.fireflow.editor.widget;

import de.blazemcworld.fireflow.editor.Bounds;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.editor.Widget;
import de.blazemcworld.fireflow.node.NodeCategory;
import de.blazemcworld.fireflow.util.TextWidth;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

import java.util.ArrayList;

public class CreateWidget implements Widget {

    private final static Component text = Component.text("Create node:", NamedTextColor.YELLOW, TextDecoration.ITALIC);

    private final ArrayList<ButtonWidget> buttons;
    private final RectWidget border;
    private final Bounds bounds;
    private final TextWidget label;

    public CreateWidget(Vec pos, Instance inst) {
        Vec originPos = pos;
        buttons = new ArrayList<>(NodeCategory.CATEGORIES.length);
        double height = NodeCategory.CATEGORIES.length * 0.3;
        double width = 80;
        for (NodeCategory c : NodeCategory.CATEGORIES) width = Math.max(width, TextWidth.calculate(c.name(), false));
        width /= 40;
        bounds = new Bounds(
                pos.add(-width / 2 - 0.1, height / 2 + 0.15, 0),
                pos.add(width / 2 + 0.1, height / 2 - 0.05, 0)
        );
        border = new RectWidget(inst, bounds);

        pos = pos.add(width / 2, height / 2 - 0.25, 0);
        label = new TextWidget(pos, inst, text);

        for (NodeCategory c : NodeCategory.CATEGORIES) {
            pos = pos.add(0, -0.3, 0);
            ButtonWidget btn = new ButtonWidget(pos, inst, Component.text("» " + c.name(), NamedTextColor.AQUA));
            btn.rightClick = (player, editor) -> {
                editor.remove(this);
                editor.widgets.add(new CategoryWidget(originPos, editor, c, text));
            };
            btn.leftClick = (player, editor) -> editor.remove(this);
            buttons.add(btn);
        }
    }

    @Override
    public Widget select(Player player, Vec cursor) {
        for (ButtonWidget button : buttons) {
            Widget result = button.select(player, cursor);
            if (result != null) return result;
        }
        return bounds.includes2d(cursor) ? this : null;
    }

    @Override
    public void leftClick(Vec cursor, Player player, CodeEditor editor) {
        editor.remove(this);
    }

    @Override
    public void remove() {
        label.remove();
        for (TextWidget button : buttons) {
            button.remove();
        }
        border.remove();
    }
}
