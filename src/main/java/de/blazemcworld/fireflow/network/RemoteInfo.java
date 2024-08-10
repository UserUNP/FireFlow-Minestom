package de.blazemcworld.fireflow.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.space.SpaceInfo;
import de.blazemcworld.fireflow.util.Config;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class RemoteInfo {

    public static List<ServerInfo> list = new ArrayList<>();
    public static List<SpaceInfo> active = new ArrayList<>();
    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public static void init() {
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            new Thread(RemoteInfo::updateList).start();
            return TaskSchedule.minutes(10);
        });
        MinecraftServer.getSchedulerManager().submitTask(() -> {
            new Thread(RemoteInfo::updateActive).start();
            return TaskSchedule.minutes(1);
        });
    }

    private static void updateActive() {
        List<SpaceInfo> updated = new ArrayList<>();

        JsonObject payload = new JsonObject();
        payload.addProperty("type", "active_spaces");

        List<Pair<ServerInfo, JsonElement>> responses = fetchAll(payload);

        for (Pair<ServerInfo, JsonElement> res : responses) {
            for (JsonElement space : res.second().getAsJsonArray()) {
                updated.add(readSpace(space.getAsJsonObject(), res.first()));
            }
        }

        active = updated;
    }

    private static List<Pair<ServerInfo, JsonElement>> fetchAll(JsonObject payload) {
        List<Pair<ServerInfo, JsonElement>> fetched = new ArrayList<>();

        List<Pair<ServerInfo, CompletableFuture<HttpResponse<String>>>> responses = new ArrayList<>();
        for (ServerInfo server : list) {
            responses.add(Pair.of(server, client.sendAsync(HttpRequest.newBuilder(URI.create(server.api))
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                    .build(), HttpResponse.BodyHandlers.ofString())));
        }

        for (Pair<ServerInfo, CompletableFuture<HttpResponse<String>>> res : responses) {
            try {
                fetched.add(Pair.of(res.first(), JsonParser.parseString(res.second().join().body())));
            } catch (CompletionException err) {
                FireFlow.LOGGER.error("Failed to fetch from {}", res.first().api, err.getCause());
            }
        }

        return fetched;
    }

    public static List<SpaceInfo> queryOwned(Player player) {
        List<SpaceInfo> owned = new ArrayList<>();

        JsonObject payload = new JsonObject();
        payload.addProperty("type", "owned_spaces");
        payload.addProperty("player", player.getUuid().toString());

        List<Pair<ServerInfo, JsonElement>> responses = fetchAll(payload);

        for (Pair<ServerInfo, JsonElement> res : responses) {
            for (JsonElement space : res.second().getAsJsonArray()) {
                owned.add(readSpace(space.getAsJsonObject(), res.first()));
            }
        }

        return owned;
    }

    private static SpaceInfo readSpace(JsonObject object, ServerInfo server) {
        SpaceInfo info = new SpaceInfo();
        info.id = object.get("id").getAsInt();
        info.title = object.get("title").getAsString();
        info.owner = UUID.fromString(object.get("owner").getAsString());
        info.icon = Material.fromNamespaceId(object.get("icon").getAsString());
        if (info.icon == null) info.icon = Material.PAPER;
        info.remotePlayers = object.get("players").getAsInt();
        info.server = server;
        return info;
    }

    private static void updateList() {
        List<ServerInfo> updated = new ArrayList<>();
        for (String source : Config.store.network().sources()) {
            try {
                HttpResponse<String> res = client.send(HttpRequest.newBuilder(URI.create(source)).build(), HttpResponse.BodyHandlers.ofString());
                JsonArray info = JsonParser.parseString(res.body()).getAsJsonArray();

                add:
                for (JsonElement e : info) {
                    Material icon = Material.fromNamespaceId(e.getAsJsonObject().get("icon").getAsString());
                    ServerInfo next = new ServerInfo(
                            e.getAsJsonObject().get("name").getAsString(),
                            icon == null ? Material.PAPER : icon,
                            e.getAsJsonObject().get("mcHost").getAsString(),
                            e.getAsJsonObject().get("mcPort").getAsInt(),
                            e.getAsJsonObject().get("api").getAsString()
                    );
                    if (next.mcHost.equals(Config.store.network().mcHost()) && next.mcPort == Config.store.port()) continue;
                    for (ServerInfo server : list) {
                        if (server.mcHost.equals(next.mcHost) && server.mcPort == next.mcPort) {
                            continue add;
                        }
                    }
                    updated.add(next);
                }
            } catch (Exception err) {
                FireFlow.LOGGER.error("Failed to fetch server list from {}", source, err);
            }
        }

        list = updated;
    }

    public record ServerInfo(String name, Material icon, String mcHost, int mcPort, String api) {
        public ItemStack buildItem() {
            List<Component> lore = new ArrayList<>();
            if (mcPort == 25565) {
                lore.add(Component.text(mcHost).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            } else {
                lore.add(Component.text(mcHost + ":" + mcPort).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            }
            return ItemStack.builder(icon)
                    .customName(MiniMessage.miniMessage().deserialize(name)
                            .decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                    .lore(lore)
                    .build();
        }
    }
}
