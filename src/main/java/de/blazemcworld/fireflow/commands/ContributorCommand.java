package de.blazemcworld.fireflow.commands;

import de.blazemcworld.fireflow.FireFlow;
import de.blazemcworld.fireflow.space.Space;
import de.blazemcworld.fireflow.space.SpaceManager;
import de.blazemcworld.fireflow.util.Messages;
import de.blazemcworld.fireflow.util.Transfer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentString;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.mojang.MojangUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContributorCommand extends Command {

    public ContributorCommand() {
        super("contributor");

        addSyntax((sender, ctx) -> {
            handle(sender, "add", ctx.get("player"));
        }, new ArgumentLiteral("add"), new ArgumentString("player"));

        addSyntax((sender, ctx) -> {
            handle(sender, "remove", ctx.get("player"));
        }, new ArgumentLiteral("remove"), new ArgumentString("player"));

        addSyntax((sender, ctx) -> {
            handle(sender, "list", null);
        }, new ArgumentLiteral("list"));
    }

    private void handle(CommandSender sender, String action, String input) {
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

            if (action.equals("list")) {
                new Thread(() -> {
                    List<String> names = new ArrayList<>();
                    try {
                        for (UUID uuid : space.info.contributors) {
                            names.add(MojangUtils.getUsername(uuid));
                        }
                    } catch (Exception err) {
                        FireFlow.LOGGER.warn("Failed to list contributors.", err);
                        sender.sendMessage(Messages.error("Failed to list contributors!"));
                        return;
                    }
                    if (names.isEmpty()) {
                        names.add("<none>");
                    }
                    sender.sendMessage(Messages.success("Contributors: " + String.join(", ", names)));
                }).start();
                return;
            }

            new Thread(() -> {
                UUID uuid;
                try {
                    uuid = MojangUtils.getUUID(input);
                } catch (Exception err) {
                    FireFlow.LOGGER.warn("Failed to resolve player!", err);
                    sender.sendMessage(Messages.error("Unknown player!"));
                    return;
                }

                if (uuid.equals(player.getUuid())) {
                    sender.sendMessage(Messages.error("You cannot change your own permissions!"));
                    return;
                }

                switch (action) {
                    case "add" -> {
                        if (space.info.contributors.contains(uuid)) {
                            sender.sendMessage(Messages.error("Player is already a contributor!"));
                            return;
                        }
                        space.info.contributors.add(uuid);
                        sender.sendMessage(Messages.success("Added contributor!"));
                    }
                    case "remove" -> {
                        if (!space.info.contributors.contains(uuid)) {
                            sender.sendMessage(Messages.error("Player is not a contributor!"));
                            return;
                        }
                        space.info.contributors.remove(uuid);
                        sender.sendMessage(Messages.success("Removed contributor!"));

                        if (space.code.getPlayerByUuid(uuid) != null) {
                            Transfer.movePlayer(space.code.getPlayerByUuid(uuid), space.play);
                        }
                    }
                }
            }).start();
        } else {
            sender.sendMessage(Messages.error("Only players can do this!"));
        }
    }
}
