package de.blazemcworld.fireflow.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Messages {

    public static Component error(String message) {
        return Component.text(message).color(NamedTextColor.RED);
    }

    public static Component success(String message) {
        return Component.text(message).color(NamedTextColor.GREEN);
    }
}
