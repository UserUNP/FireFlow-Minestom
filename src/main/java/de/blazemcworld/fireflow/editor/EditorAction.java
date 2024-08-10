package de.blazemcworld.fireflow.editor;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.player.PlayerChatEvent;

public interface EditorAction {

    default void leftClick(Vec cursor) {}
    default void rightClick(Vec cursor) {}
    default void tick(Vec cursor) {}
    default void stop() {}
    default void chat(Vec cursor, PlayerChatEvent event) {}
    default void swapItem(Vec cursor) {}
}
