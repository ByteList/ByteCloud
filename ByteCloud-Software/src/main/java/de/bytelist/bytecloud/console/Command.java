package de.bytelist.bytecloud.console;

import lombok.Getter;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class Command {

    /**
     * Gets the name from this command.
     */
    @Getter
    private final String name;
    /**
     * Gets the description from this command.
     */
    @Getter
    private final String description;


    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void execute(String[] args);

}
