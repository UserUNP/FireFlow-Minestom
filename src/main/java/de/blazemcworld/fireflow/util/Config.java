package de.blazemcworld.fireflow.util;

import com.google.gson.Gson;
import de.blazemcworld.fireflow.FireFlow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Config {

    public static final Store store = readConfig();

    private static Store readConfig() {
        try {
            Store store = new Gson().fromJson(Files.readString(Path.of("config.json")), Store.class);
            if (store.motd == null) FireFlow.LOGGER.warn("'motd' in config is not set!");
            if (store.port == 0) FireFlow.LOGGER.warn("Invalid 'port' in config.json!");

            if (store.limits == null) {
                FireFlow.LOGGER.warn("Missing 'limits' in config.json!");
            } else {
                if (store.limits.cpuPerTick <= 0) {
                    FireFlow.LOGGER.warn("Invalid 'limits.cpuPerTick' in config.json!");
                } else if (store.limits.cpuPerTick <= 100000) {
                    FireFlow.LOGGER.warn("Warning: 'limits.cpuPerTick' is low! Currently set to {}ns = {}ms!", store.limits.cpuPerTick, store.limits.cpuPerTick / 1000000.0);
                }
                if (store.limits.spacesPerPlayer <= 0) FireFlow.LOGGER.warn("'limits.spacesPerPlayer' does not allow creating new spaces!");
                if (store.limits.totalSpaces <= 0) FireFlow.LOGGER.warn("'limits.totalSpaces' does not allow creating new spaces!");
            }

            if (store.network == null) {
                FireFlow.LOGGER.warn("Missing 'network' in config.json!");
            } else {
                if (store.network.enabled) {
                    if (store.network.port <= 0) FireFlow.LOGGER.warn("Invalid 'network.port' in config.json!");
                    if (store.network.sources == null) FireFlow.LOGGER.warn("Missing 'network.sources' in config.json!");
                    if (store.network.mcHost == null) FireFlow.LOGGER.warn("Missing 'network.mcHost' in config.json!");
                }
            }
            return store;
        } catch (IOException e) {
            FireFlow.LOGGER.error("Error reading config.json!");
            throw new RuntimeException(e);
        }
    }

    public record Store(String motd, int port, LimitsConfig limits, NetworkConfig network) {
    }

    public record LimitsConfig(long cpuPerTick, int spacesPerPlayer, int totalSpaces) {
    }

    public record NetworkConfig(boolean enabled, int port, List<String> sources, String mcHost) {
    }
}
