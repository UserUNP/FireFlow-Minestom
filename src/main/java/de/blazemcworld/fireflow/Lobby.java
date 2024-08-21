package de.blazemcworld.fireflow;

import de.blazemcworld.fireflow.inventory.MySpacesInventory;
import de.blazemcworld.fireflow.inventory.PreferencesInventory;
import de.blazemcworld.fireflow.inventory.ServerListInventory;
import de.blazemcworld.fireflow.inventory.SpacesListInventory;
import de.blazemcworld.fireflow.network.RemoteInfo;
import de.blazemcworld.fireflow.space.SpaceInfo;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.space.SpacesIndex;
import de.blazemcworld.fireflow.util.Config;
import de.blazemcworld.fireflow.util.Statistics;
import de.blazemcworld.fireflow.util.Transfer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.event.trait.PlayerInstanceEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

import java.nio.ByteBuffer;
import java.util.*;

public class Lobby {

    public static final InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();

    private static final ItemStack MY_SPACES = ItemStack.builder(Material.ENCHANTED_BOOK)
            .customName(Component.text("My Spaces").color(NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false))
            .lore(
                    Component.text("Manage your spaces").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.text("using this item.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .build();

    private static final ItemStack ACTIVE_SPACES = ItemStack.builder(Material.BLAZE_POWDER)
            .customName(Component.text("Active Spaces").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .lore(
                    Component.text("Browse currently played").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.text("spaces using this item.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .build();

    private static final ItemStack OTHER_SERVERS = ItemStack.builder(Material.WARPED_SIGN)
            .customName(Component.text("Other Servers").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false))
            .lore(
                    Component.text("Browse all servers").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.text("using this item.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .build();

    private static final ItemStack PREFERENCES_ITEM = ItemStack.builder(Material.PIGLIN_BANNER_PATTERN)
            .customName(Component.text("Preferences").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false))
            .lore(
                    Component.text("Manage your preferences").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                    Component.text("using this item.").color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
            )
            .hideExtraTooltip()
            .build();

    static {
        instance.setChunkSupplier(LightingChunk::new);

        instance.setGenerator(unit -> {
            if (Math.abs(unit.absoluteStart().x() + 8) > 16) return;
            if (Math.abs(unit.absoluteStart().z() + 8) > 16) return;
            unit.modifier().fillHeight(-1, 0, Block.POLISHED_ANDESITE);
        });

        instance.setTimeRate(0);

        EventNode<InstanceEvent> events = instance.eventNode();

        events.addListener(PlayerMoveEvent.class, event -> {
            if (event.getNewPosition().y() < -20) {
                event.setNewPosition(Pos.ZERO);
            }
        });

        events.addListener(PlayerSpawnEvent.class, event -> {
            Player player = event.getPlayer();
            Statistics.reset(player);
            PlayerInventory inv = player.getInventory();
            inv.setItemStack(0, MY_SPACES);
            inv.setItemStack(4, ACTIVE_SPACES);
            if (Config.store.network().enabled()) {
                inv.setItemStack(7, PREFERENCES_ITEM);
                inv.setItemStack(8, OTHER_SERVERS);
            } else inv.setItemStack(8, PREFERENCES_ITEM);

            player.getPlayerConnection().fetchCookie("fireflow_code_space").thenAccept(bytes -> {
                if (bytes == null || bytes.length == 0) return;
                player.getPlayerConnection().storeCookie("fireflow_code_space", new byte[0]);
                int id = ByteBuffer.wrap(bytes).getInt();
                for (SpaceInfo info : SpacesIndex.spaces) {
                    if (info.id != id) continue;
                    if (!info.owner.equals(player.getUuid()) && !info.contributors.contains(player.getUuid())) return;
                    Transfer.movePlayer(player, SpaceManager.getSpace(info).code);
                    return;
                }
            });
            player.getPlayerConnection().fetchCookie("fireflow_play_space").thenAccept(bytes -> {
                if (bytes == null || bytes.length == 0) return;
                player.getPlayerConnection().storeCookie("fireflow_play_space", new byte[0]);
                int id = ByteBuffer.wrap(bytes).getInt();
                for (SpaceInfo info : SpacesIndex.spaces) {
                    if (info.id != id) continue;
                    Transfer.movePlayer(player, SpaceManager.getSpace(info).play);
                    return;
                }
            });
        });

        events.addListener(PlayerUseItemEvent.class, event -> {
            event.setCancelled(true);
            rightClick(event);
        });
        events.addListener(PlayerUseItemOnBlockEvent.class, Lobby::rightClick);

        events.addListener(InstanceEvent.class, event -> {
            if (event instanceof BlockEvent && event instanceof CancellableEvent c) {
                c.setCancelled(true);
            }
        });
        events.addListener(ItemDropEvent.class, event -> event.setCancelled(true));
        events.addListener(InventoryPreClickEvent.class, event -> event.setCancelled(true));
        events.addListener(PlayerSwapItemEvent.class, event -> event.setCancelled(true));

        MinecraftServer.getSchedulerManager().submitTask(() -> {
            Set<Chunk> unload = new HashSet<>();
            for (Chunk c : instance.getChunks()) {
                if (c.getViewers().isEmpty()) unload.add(c);
            }
            for (Chunk c : unload) instance.unloadChunk(c);
            return TaskSchedule.minutes(1);
        });
    }

    private static void rightClick(PlayerInstanceEvent event) {
        if (event.getPlayer().getItemInMainHand().isSimilar(MY_SPACES)) {
            MySpacesInventory.open(event.getPlayer());
        }
        if (event.getPlayer().getItemInMainHand().isSimilar(ACTIVE_SPACES)) {
            List<SpaceInfo> active = new ArrayList<>();
            active.addAll(SpaceManager.activeInfo());
            active.addAll(RemoteInfo.active);
            active.sort(Comparator.<SpaceInfo, Integer>comparing((info) -> info.server == null ? SpaceManager.playerCount(info.id) : info.remotePlayers)
                    .thenComparing(s -> s.title));
            SpacesListInventory.open(event.getPlayer(), "Active Spaces", active);
        }
        if (event.getPlayer().getItemInMainHand().isSimilar(OTHER_SERVERS)) {
            ServerListInventory.open(event.getPlayer());
        }
        if (event.getPlayer().getItemInMainHand().isSimilar(PREFERENCES_ITEM)) {
            PreferencesInventory.open(event.getPlayer());
        }
    }

}
