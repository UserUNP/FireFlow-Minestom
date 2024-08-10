package de.blazemcworld.fireflow.space;

import de.blazemcworld.fireflow.network.RemoteInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.NetworkBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpaceInfo {
    public int id = -1;
    public String title = "Unnamed Space";
    public Material icon = Material.PAPER;
    public UUID owner = null;
    public List<UUID> contributors = new ArrayList<>();
    public RemoteInfo.ServerInfo server = null;
    public int remotePlayers = -1;

    public void write(NetworkBuffer buffer) {
        buffer.write(NetworkBuffer.INT, id);
        buffer.write(NetworkBuffer.STRING, title);
        buffer.write(NetworkBuffer.STRING, icon.namespace().path());
        buffer.write(NetworkBuffer.UUID, owner);
        buffer.write(NetworkBuffer.INT, contributors.size());
        for (UUID contributor : contributors) {
            buffer.write(NetworkBuffer.UUID, contributor);
        }
    }

    public void read(NetworkBuffer buffer) {
        id = buffer.read(NetworkBuffer.INT);
        title = buffer.read(NetworkBuffer.STRING);
        icon = Material.fromNamespaceId(buffer.read(NetworkBuffer.STRING));
        if (icon == null) icon = Material.PAPER;
        owner = buffer.read(NetworkBuffer.UUID);
        int size = buffer.read(NetworkBuffer.INT);
        for (int i = 0; i < size; i++) {
            contributors.add(buffer.read(NetworkBuffer.UUID));
        }
    }

    public ItemStack buildItem() {
        return ItemStack.builder(icon)
                .customName(MiniMessage.miniMessage().deserialize(title)
                        .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                .lore(Component.text("ID: " + id).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .lore(Component.text(server == null ? "Current server" : "From " + server.name()).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                .build();
    }
}
