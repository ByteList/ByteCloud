package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.console.CommandHandler;

/**
 * Created by ByteList on 10.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "list all commands");
    }

    private final CommandHandler commandHandler = ByteCloud.getInstance().getCommandHandler();

    @Override
    public void execute(String[] args) {
        System.out.println("All cloud commands: ");
        for(String command : commandHandler.getCommands().keySet()) {
            System.out.println(command+" - "+commandHandler.getCommand(command).getDescription());
        }
    }
}
