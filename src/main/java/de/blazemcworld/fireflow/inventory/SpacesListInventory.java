package de.blazemcworld.fireflow.inventory;

import de.blazemcworld.fireflow.space.SpaceInfo;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Transfer;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.network.packet.server.common.TransferPacket;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

public class SpacesListInventory {

    public static void open(Player player, String title, List<SpaceInfo> spaces) {
        Inventory inv = new Inventory(InventoryType.CHEST_3_ROW, title);
        HashMap<Integer, SpaceInfo> slot2spaceMap = new HashMap<>();
        int spaceCounter = 0;
        for (SpaceInfo info : spaces) {
            inv.setItemStack(spaceCounter, info.buildItem());
            slot2spaceMap.put(spaceCounter, info);
            spaceCounter++;
            if (spaceCounter >= inv.getInventoryType().getSize()) break;
        }
        inv.addInventoryCondition((who, slot, type, click) -> {
            click.setCancel(true);
            if (who != player) return;
            if (!slot2spaceMap.containsKey(slot)) return;
            
            SpaceInfo info = slot2spaceMap.get(slot);
            if (info.server == null) {
                Transfer.movePlayer(player, SpaceManager.getSpace(info).play);
            } else {
                player.getPlayerConnection().storeCookie("fireflow_play_space", ByteBuffer.allocate(4).putInt(info.id).array());
                player.sendPacket(new TransferPacket(
                        info.server.mcHost(),
                        info.server.mcPort()
                ));
            }
            
        });
        player.openInventory(inv);
    }
}
