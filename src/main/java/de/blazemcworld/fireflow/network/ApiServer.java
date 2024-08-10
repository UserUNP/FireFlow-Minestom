package de.blazemcworld.fireflow.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.space.SpaceInfo;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.space.SpacesIndex;
import de.blazemcworld.fireflow.util.Config;
import net.minestom.server.MinecraftServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

public class ApiServer {

    public static void init() {
        if (!Config.store.network().enabled()) return;
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress("0.0.0.0", Config.store.network().port()), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        server.createContext("/", new Handler());
        server.setExecutor(null);
        server.start();

        FireFlow.LOGGER.info("API server listening on port {}", Config.store.network().port());
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> server.stop(0));
    }

    private static class Handler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equals("POST")) {
                exchange.sendResponseHeaders(405, 0);
                return;
            }

            try {
                JsonObject request = JsonParser.parseString(new String(exchange.getRequestBody().readNBytes(1024 * 1024))).getAsJsonObject();

                JsonElement response = null;
                switch (request.get("type").getAsString()) {
                    case "active_spaces" -> {
                        JsonArray res = new JsonArray();
                        List<SpaceInfo> list = SpaceManager.activeInfo();
                        for (SpaceInfo info : list) {
                            res.add(toJson(info));
                        }
                        response = res;
                    }
                    case "owned_spaces" -> {
                        UUID player = UUID.fromString(request.get("player").getAsString());
                        JsonArray res = new JsonArray();
                        for (SpaceInfo space : SpacesIndex.spaces) {
                            if (space.owner.equals(player)) {
                                res.add(toJson(space));
                            }
                        }
                        response = res;
                    }
                }

                if (response != null) {
                    byte[] bytes = response.toString().getBytes();
                    exchange.sendResponseHeaders(200, bytes.length);
                    exchange.getResponseBody().write(bytes);
                    exchange.close();
                } else {
                    exchange.sendResponseHeaders(400, 0);
                }
            } catch (Exception err) {
                FireFlow.LOGGER.warn("Exception while handling request!", err);
                exchange.sendResponseHeaders(400, 0);
            }
        }
    }

    private static JsonObject toJson(SpaceInfo info) {
        JsonObject object = new JsonObject();
        object.addProperty("id", info.id);
        object.addProperty("title", info.title);
        object.addProperty("icon", info.icon.namespace().path());
        object.addProperty("owner", info.owner.toString());
        object.addProperty("players", SpaceManager.playerCount(info.id));
        return object;
    }

}
