package de.blazemcworld.fireflow.inventory;

import de.blazemcworld.fireflow.network.RemoteInfo;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.network.packet.server.common.TransferPacket;

import java.util.HashMap;
import java.util.Map;

public class ServerListInventory {
    public static void open(Player player) {
        Inventory inventory = new Inventory(InventoryType.CHEST_3_ROW, "Server List");

        int id = 0;
        Map<Integer, RemoteInfo.ServerInfo> servers = new HashMap<>();
        for (RemoteInfo.ServerInfo server : RemoteInfo.list) {
            inventory.setItemStack(id, server.buildItem());
            servers.put(id, server);
            id++;
        }

        inventory.addInventoryCondition((who, slot, type, click) -> {
            click.setCancel(true);
            if (who != player) return;
            if (!servers.containsKey(slot)) return;
            player.sendPacket(new TransferPacket(
                    servers.get(slot).mcHost(),
                    servers.get(slot).mcPort()
            ));
        });

        player.openInventory(inventory);
    }
}
