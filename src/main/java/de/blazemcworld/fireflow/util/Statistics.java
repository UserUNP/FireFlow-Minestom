package de.blazemcworld.fireflow.util;

import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class Statistics {

    public static void reset(Player player) {
        player.closeInventory();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlying(false);
        player.getInventory().clear();
    }

}
