package de.blazemcworld.fireflow;

import de.blazemcworld.fireflow.commands.*;
import de.blazemcworld.fireflow.network.ApiServer;
import de.blazemcworld.fireflow.network.RemoteInfo;
import de.blazemcworld.fireflow.space.SpacesIndex;
import de.blazemcworld.fireflow.util.Config;
import de.blazemcworld.fireflow.util.PlayerExitInstanceEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.ping.ResponseData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class FireFlow {

    public static final Logger LOGGER = LogManager.getLogger("FireFlow");

    public static void main(String[] args) {
        LOGGER.info("Starting...");
        MinecraftServer server = MinecraftServer.init();

        MinecraftServer.setBrandName("FireFlow");
        MojangAuth.init();
        ConsoleHandler.init();
        SpacesIndex.init();

        CommandManager cmds = MinecraftServer.getCommandManager();
        cmds.register(new PlayCommand());
        cmds.register(new CodeCommand());
        cmds.register(new LobbyCommand());
        cmds.register(new ReloadCommand());
        cmds.register(new JoinCommand());
        cmds.register(new SetTitleCommand());
        cmds.register(new SetIconCommand());
        cmds.register(new ContributorCommand());

        if (Config.store.network().enabled()) {
            ApiServer.init();
            RemoteInfo.init();
        }

        GlobalEventHandler events = MinecraftServer.getGlobalEventHandler();

        events.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(Lobby.instance);
            event.getPlayer().setGameMode(GameMode.CREATIVE);
        });

        events.addListener(PlayerDisconnectEvent.class, event -> {
            MinecraftServer.getGlobalEventHandler().call(new PlayerExitInstanceEvent(event.getPlayer()));
        });

        events.addListener(ServerListPingEvent.class, event -> {
            ResponseData res = new ResponseData();
            res.setDescription(MiniMessage.miniMessage().deserialize(Config.store.motd()));

            try {
                Path favicon = Path.of("favicon.png");
                if (Files.exists(favicon)) res.setFavicon("data:image/png;base64," + Base64.getEncoder().encodeToString(Files.readAllBytes(favicon)));
            } catch (IOException e) {
                LOGGER.error(e);
            }

            event.setResponseData(res);
        });

        TransferPacketPatcher.apply();

        server.start("0.0.0.0", Config.store.port());
        LOGGER.info("Started on port {}!", Config.store.port());
    }

}
