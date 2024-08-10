package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Messages;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.minecraft.ArgumentItemStack;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

public class SetIconCommand extends Command {

    public SetIconCommand() {
        super("seticon");
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
                ItemStack icon = ctx.get("icon");
                if (icon == null || icon.isAir()) {
                    sender.sendMessage(Messages.error("Invalid item id!"));
                    return;
                }
                space.info.icon = icon.material();
                sender.sendMessage(Messages.success("Changed space icon!"));
            } else {
                sender.sendMessage(Messages.error("Only players can do this!"));
            }
        }, new ArgumentItemStack("icon"));
    }

}
