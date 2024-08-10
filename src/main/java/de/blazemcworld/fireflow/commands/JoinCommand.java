package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.space.SpaceInfo;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.space.SpacesIndex;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.util.Transfer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.number.ArgumentInteger;
import net.minestom.server.entity.Player;

public class JoinCommand extends Command {

    public JoinCommand() {
        super("join");

        addSyntax((sender, ctx) -> {
            if (sender instanceof Player player) {
                for (SpaceInfo info : SpacesIndex.spaces) {
                    if (info.id != ctx.<Integer>get("id")) continue;
                    Transfer.movePlayer(player, SpaceManager.getSpace(info).play);
                    return;
                }
                sender.sendMessage(Messages.error("Space not found!"));
            } else {
                sender.sendMessage(Messages.error("Only players can do this!"));
            }
        }, new ArgumentInteger("id"));
    }

}
