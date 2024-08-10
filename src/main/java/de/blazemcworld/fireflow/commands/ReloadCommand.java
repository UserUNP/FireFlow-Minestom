package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Messages;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("reload");

        setDefaultExecutor((sender, ctx) -> {
            if (sender instanceof Player player) {
                Space space = SpaceManager.getSpace(player);
                if (space == null) {
                    sender.sendMessage(Messages.error("You must be in a space to do this!"));
                    return;
                }
                space.reload();
                sender.sendMessage(Messages.success("Reloaded!"));
            }
        });
    }
}
