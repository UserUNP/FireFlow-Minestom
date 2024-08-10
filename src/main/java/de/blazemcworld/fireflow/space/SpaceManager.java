package de.blazemcworld.fireflow.space;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SpaceManager {

    private static final HashMap<Integer, Space> spaces = new HashMap<>();

    static {
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
            for (Space space : spaces.values()) {
                space.unregister();
            }
        });
    }

    public static Space getSpace(SpaceInfo info) {
        return spaces.computeIfAbsent(info.id, id -> new Space(info));
    }

    public static void forget(int id) {
        spaces.remove(id);
    }

    public static @Nullable Space getSpace(Player player) {
        for (Space s : spaces.values()) {
            if (s.play.getPlayers().contains(player) || s.code.getPlayers().contains(player)) {
                return s;
            }
        }
        return null;
    }

    public static List<SpaceInfo> activeInfo() {
        List<SpaceInfo> list = new ArrayList<>();
        for (Space space : spaces.values()) {
            if (!space.play.getPlayers().isEmpty() || !space.code.getPlayers().isEmpty()) list.add(space.info);
        }
        return list;
    }

    public static int playerCount(int id) {
        Space space = spaces.get(id);
        if (space == null) return 0;
        return space.play.getPlayers().size() + space.code.getPlayers().size();
    }
}
