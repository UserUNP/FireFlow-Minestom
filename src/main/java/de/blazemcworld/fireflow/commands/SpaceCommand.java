package de.blazemcworld.fireflow.commands;

import net.minestom.server.command.builder.Command;

public class SpaceCommand extends Command {
    public SpaceCommand() {
        super("space");

        addSubcommand(new SetTitleCommand());
        addSubcommand(new SetIconCommand());
        addSubcommand(new ContributorCommand());
        addSubcommand(new VarCommand());
    }
}
