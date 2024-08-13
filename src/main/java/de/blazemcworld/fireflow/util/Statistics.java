package de.blazemcworld.fireflow.util;

import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.attribute.AttributeInstance;

public class Statistics {

    public static void reset(Player player) {
        player.closeInventory();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setAllowFlying(false);
        player.getInventory().clear();
        player.setExp(0);
        player.setFood(20);
        player.setFoodSaturation(20);
        player.setInvulnerable(false);
        player.setLevel(0);
        player.setHealth(20);
        for (AttributeInstance attr : player.getAttributes()) {
            attr.clearModifiers();
        }
    }

}
