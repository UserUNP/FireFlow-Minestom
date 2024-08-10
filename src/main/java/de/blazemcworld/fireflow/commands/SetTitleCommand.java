package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Messages;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.entity.Player;

public class SetTitleCommand extends Command {

    public SetTitleCommand() {
        super("settitle");
        addSyntax((sender, ctx) -> {
            if (sender instanceof Player player) {
                Space space = SpaceManager.getSpace(player);
                if (space == null) {
                    sender.sendMessage(Messages.error("You must be in a space to do this!"));
                    return;
                }
                if (!space.info.owner.equals(player.getUuid())) {
                    sender.sendMessage(Messages.error("You do not own this space!"));
                    return;
                }
                space.info.title = ctx.get("title");
                sender.sendMessage(Messages.success("Changed space title!"));
            } else {
                sender.sendMessage(Messages.error("Only players can do this!"));
            }
        }, new ArgumentString("title"));
    }

}
