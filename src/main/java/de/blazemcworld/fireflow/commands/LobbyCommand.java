package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.Lobby;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.util.Transfer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", "spawn");

        setDefaultExecutor((sender, ctx) -> {
            if (sender instanceof Player player) {
                if (player.getInstance() == Lobby.instance) {
                    sender.sendMessage(Messages.error("You are already in the lobby!"));
                    return;
                }
                Transfer.movePlayer(player, Lobby.instance);
            } else {
                sender.sendMessage(Messages.error("Only players can do this!"));
            }
        });
    }
}
