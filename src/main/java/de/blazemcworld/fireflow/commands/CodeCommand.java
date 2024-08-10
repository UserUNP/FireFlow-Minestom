package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.util.Transfer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

import java.util.UUID;

public class CodeCommand extends Command {

    public CodeCommand() {
        super("code", "dev");

        setDefaultExecutor((sender, ctx) -> {
            if (sender instanceof Player player) {
                Space space = SpaceManager.getSpace(player);
                if (space == null) {
                    sender.sendMessage(Messages.error("You must be in a space to do this!"));
                    return;
                }
                if (player.getInstance() == space.code) {
                    sender.sendMessage(Messages.error("You are already coding!"));
                    return;
                }
                if (!space.info.owner.equals(player.getUuid())) {
                    boolean allowed = false;
                    for (UUID contributor : space.info.contributors) {
                        if (contributor.equals(player.getUuid())) {
                            allowed = true;
                            break;
                        }
                    }
                    if (!allowed) {
                        sender.sendMessage(Messages.error("You are not allowed to do that!"));
                        return;
                    }
                }
                Transfer.movePlayer(player, space.code);
            } else {
                sender.sendMessage(Messages.error("Only players can do this!"));
            }
        });
    }
}
