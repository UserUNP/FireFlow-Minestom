package de.blazemcworld.fireflow.inventory;

import de.blazemcworld.fireflow.Lobby;
import de.blazemcworld.fireflow.network.RemoteInfo;
import de.blazemcworld.fireflow.space.SpaceInfo;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.space.SpacesIndex;
import de.blazemcworld.fireflow.util.Config;
import de.blazemcworld.fireflow.util.Transfer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.common.TransferPacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MySpacesInventory {
    private static final ItemStack NEW_SPACE_ITEM = ItemStack.builder(Material.GREEN_STAINED_GLASS)
            .customName(Component.text("New Space").color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false)).build();

    public static void open(Player player) {
        new Thread(() -> {
            List<SpaceInfo> remoteOwned = RemoteInfo.queryOwned(player);
            if (Lobby.instance != player.getInstance()) return;
            openLocal(player, remoteOwned);
        }).start();
    }

    public static void openLocal(Player player, List<SpaceInfo> remoteOwned) {
        Inventory inv = new Inventory(InventoryType.CHEST_3_ROW, "My Spaces");
        HashMap<Integer, SpaceInfo> slot2spaceMap = new HashMap<>();

        List<SpaceInfo> owned = new ArrayList<>();
        for (SpaceInfo info : SpacesIndex.spaces) {
            if (!info.owner.equals(player.getUuid())) continue;
            owned.add(info);
        }
        int locallyOwned = owned.size();

        owned.addAll(remoteOwned);
        owned.sort(Comparator.<SpaceInfo, Integer>comparing(info -> info.server == null ? SpaceManager.playerCount(info.id) : info.remotePlayers)
                .thenComparing(s -> s.title));

        int spaceCounter = 0;
        for (SpaceInfo info : owned) {
            inv.setItemStack(spaceCounter, info.buildItem());
            slot2spaceMap.put(spaceCounter, info);
            spaceCounter++;
        }

        if (locallyOwned < Config.store.limits().spacesPerPlayer()) {
            inv.setItemStack(26, NEW_SPACE_ITEM);
        }
        inv.addInventoryCondition((who, slot, type, click) -> {
            click.setCancel(true);
            if (who != player) return;

            if (locallyOwned < Config.store.limits().spacesPerPlayer() && slot == 26) {
                int updated = 0;
                for (SpaceInfo info : SpacesIndex.spaces) {
                    if (info.owner.equals(player.getUuid())) updated++;
                }
                if (updated >= Config.store.limits().totalSpaces()) return;
                SpaceInfo info = new SpaceInfo();
                info.title = player.getUsername() + "'s Space";
                info.owner = player.getUuid();
                info.id = SpacesIndex.nextId++;
                SpacesIndex.spaces.add(info);
                MySpacesInventory.open(player);
                return;
            }

            if (!slot2spaceMap.containsKey(slot)) return;
            SpaceInfo info = slot2spaceMap.get(slot);
            String transferMode;
            if (type == ClickType.RIGHT_CLICK) {
                if (info.server == null) {
                    Transfer.movePlayer(player, SpaceManager.getSpace(info).code);
                    return;
                }
                transferMode = "code";
            } else {
                if (info.server == null) {
                    Transfer.movePlayer(player, SpaceManager.getSpace(info).play);
                    return;
                }
                transferMode = "play";
            }
            player.getPlayerConnection().storeCookie("fireflow_" + transferMode + "_space", ByteBuffer.allocate(4).putInt(info.id).array());
            player.sendPacket(new TransferPacket(
                    info.server.mcHost(),
                    info.server.mcPort()
            ));
        });
        player.openInventory(inv);
    }
}
