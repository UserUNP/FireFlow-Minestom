package de.blazemcworld.fireflow.commands;

import net.minestom.server.command.builder.Command;

public class VarCommand extends Command {

    public VarCommand() {
        super("var");

        addSubcommand(new VariablesCommand());
        addSubcommand(new SetVarCommand());
        addSubcommand(new DeleteVarCommand());
    }

}
