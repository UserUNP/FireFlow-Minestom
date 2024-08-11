package de.blazemcworld.fireflow.editor.widget;

import de.blazemcworld.fireflow.compiler.FunctionDefinition;
import de.blazemcworld.fireflow.editor.Bounds;
import de.blazemcworld.fireflow.editor.CodeEditor;
import de.blazemcworld.fireflow.editor.Widget;
import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.NodeCategory;
import de.blazemcworld.fireflow.util.TextWidth;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class NodeCategoryWidget implements Widget {

    private final List<ButtonWidget> buttons = new ArrayList<>();
    private final RectWidget border;
    private final Bounds bounds;
    public Consumer<NodeWidget> selectCallback = null;

    public NodeCategoryWidget(Vec pos, InstanceContainer inst, NodeCategory category) {
        Vec originPos = pos;
        double width = 80;
        for (NodeCategory subcategory : category.subcategories) {
            width = Math.max(width, TextWidth.calculate(subcategory.name, false));
        }
        for (Pair<String, Supplier<Node>> entry : category.nodes) {
            width = Math.max(width, TextWidth.calculate(entry.first(), false));
        }
        width /= 40;

        double height = (category.subcategories.size() + category.nodes.size()) * 0.3;
        if (category.parent != null) height += 0.3;
        if (category == NodeCategory.ROOT) height += 0.3;
        if (category.isFunctions) height += 0.3;

        bounds = new Bounds(
            Vec.fromPoint(pos).add(-width * 0.5 - 0.1, 0.5 * height + 0.15, 0),
            Vec.fromPoint(pos).add(width * 0.5 + 0.1, -0.5 * height - 0.05, 0)
        );
        border = new RectWidget(inst, bounds);

        pos = Vec.fromPoint(pos).add(width * 0.5, 0.5 * height - 0.25, 0);

        if (category.parent != null) {
            ButtonWidget btn = new ButtonWidget(pos, inst, Component.text("Back (" + category.parent.name + ")").color(NamedTextColor.YELLOW));
            btn.rightClick = (player, editor) -> {
                editor.remove(this);
                editor.widgets.add(new NodeCategoryWidget(originPos, inst, category.parent));
            };
            btn.leftClick = (player, editor) -> editor.remove(this);
            buttons.add(btn);
            pos = Vec.fromPoint(pos).add(0, -0.3, 0);
        }

        for (NodeCategory subcategory : category.subcategories) {
            ButtonWidget btn = new ButtonWidget(pos, inst, Component.text(subcategory.name).color(NamedTextColor.AQUA));
            btn.rightClick = (player, editor) -> {
                editor.remove(this);
                editor.widgets.add(new NodeCategoryWidget(originPos, inst, subcategory));
            };
            btn.leftClick = (player, editor) -> editor.remove(this);
            buttons.add(btn);
            pos = Vec.fromPoint(pos).add(0, -0.3, 0);
        }
        for (Pair<String, Supplier<Node>> entry : category.nodes) {
            ButtonWidget btn = new ButtonWidget(pos, inst, Component.text(entry.first()));
            btn.rightClick = (player, editor) -> {
                editor.remove(this);
                Node node = entry.second().get();
                if (node.possibleGenerics().isEmpty()) {
                    NodeWidget widget = new NodeWidget(originPos, inst, node);
                    editor.widgets.add(widget);
                    if (selectCallback != null) selectCallback.accept(widget);
                } else {
                    GenericSelectorWidget.choose(originPos, editor, node.possibleGenerics(), generics -> {
                        NodeWidget widget = new NodeWidget(originPos, inst, node.fromGenerics(generics));
                        editor.widgets.add(widget);
                        if (selectCallback != null) selectCallback.accept(widget);
                    });
                }
            };
            btn.leftClick = (player, editor) -> editor.remove(this);
            buttons.add(btn);
            pos = Vec.fromPoint(pos).add(0, -0.3, 0);
        }

        if (category == NodeCategory.ROOT) {
            ButtonWidget btn = new ButtonWidget(pos, inst, Component.text("Functions").color(NamedTextColor.LIGHT_PURPLE));
            btn.rightClick = (player, editor) -> {
                editor.remove(this);
                editor.widgets.add(new NodeCategoryWidget(originPos, inst, NodeCategory.forFunctions(editor)));
            };
            btn.leftClick = (player, editor) -> editor.remove(this);
            buttons.add(btn);
            pos = Vec.fromPoint(pos).add(0, -0.3, 0);
        }

        if (category.isFunctions) {
            ButtonWidget btn = new ButtonWidget(pos, inst, Component.text("Create Function").color(NamedTextColor.LIGHT_PURPLE));
            btn.rightClick = (player, editor) -> {
                editor.remove(this);

                int id = 0;
                String name = "Unnamed";
                search:
                while (true) {
                    for (FunctionDefinition definition : editor.functions) {
                        if (definition.fnName.equals(name)) {
                            name = "Unnamed " + (++id);
                            continue search;
                        }
                    }
                    break;
                }
                FunctionDefinition created = new FunctionDefinition(name, List.of(), List.of());
                editor.functions.add(created);
                editor.widgets.add(new NodeWidget(originPos.add(2, 0, 0), inst, created.fnInputsNode));
                editor.widgets.add(new NodeWidget(originPos.add(-2, 0, 0), inst, created.fnOutputsNode));
            };
            btn.leftClick = (player, editor) -> editor.remove(this);
            buttons.add(btn);
            pos = Vec.fromPoint(pos).add(0, -0.3, 0);
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
        for (TextWidget button : buttons) {
            button.remove();
        }
        border.remove();
    }
}
