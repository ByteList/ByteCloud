package de.bytelist.bytecloud.console;

import lombok.Getter;

import java.util.HashMap;
import java.util.regex.Pattern;

import static de.bytelist.bytecloud.util.Java15Compat.Arrays_copyOfRange;

/**
 * Created by ByteList on 30.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CommandHandler {

    private static final Pattern PATTERN_ON_SPACE = Pattern.compile(" ", Pattern.LITERAL);

    /**
     * Gets all registered commands.
     */
    @Getter
    private HashMap<String, Command> commands;

    public CommandHandler() {
        this.commands = new HashMap<>();
    }

    /**
     * Register a new command.
     * @param command to register
     */
    public void registerCommand(Command command) {
        this.commands.put(command.getName(), command);
    }

    /**
     * Gets the command from the name.
     * @param command line
     * @return the command from the name
     */
    public Command getCommand(String command) {
        if(command.contains(" "))
            command = command.split(" ")[0];
        return commands.getOrDefault(command, null);
    }

    /**
     * Checks if the command exist.
     * @param command line
     * @return if the command exist
     */
    public boolean existsCommand(String command) {
        if(command.contains(" "))
            command = command.split(" ")[0];
        return commands.containsKey(command);
    }

    /**
     * Execute the command from the line.
     * @param commandLine the command line
     * @return if the execute was successful
     */
    public boolean dispatchCommand(String commandLine) {
        String[] args = PATTERN_ON_SPACE.split(commandLine);

        if (args.length == 0) {
            return false;
        }

        String sentCommandLabel = args[0].toLowerCase();
        Command target = getCommand(sentCommandLabel);

        if (target == null) {
            return false;
        }

        target.execute(Arrays_copyOfRange(args, 1, args.length));

        return true;
    }
}
