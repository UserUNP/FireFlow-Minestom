package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Messages;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.entity.Player;

public class DeleteVarCommand extends Command {

    private void callback(CommandSender sender, CommandContext ctx) {
        if (sender instanceof Player player) {
            Space space = SpaceManager.getSpace(player);
            if (space == null) {
                sender.sendMessage(Messages.error("You must be in a space to do this!"));
                return;
            }
            if (!space.info.owner.equals(player.getUuid()) && !space.info.contributors.contains(player.getUuid())) {
                sender.sendMessage(Messages.error("You are not the owner nor a contributor of this space!"));
                return;
            }
            if (space.variables.get(ctx.<String>get("varname")) == null) sender.sendMessage(Messages.error("Variable \"" + ctx.get("varname") + "\" does not exist!"));
            else {
                space.variables.remove(ctx.<String>get("varname"));
                sender.sendMessage(Messages.success("Deleted variable \"" + ctx.get("varname") + "\"!"));
            }
        } else {
            sender.sendMessage(Messages.error("Only players can do this!"));
        }
    }

    public DeleteVarCommand() {
        super("delete");
        addSyntax(this::callback, new ArgumentString("varname"));
    }

}
