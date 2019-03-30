package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;

/**
 * Created by ByteList on 27.05.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class VersionCommand extends Command {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public VersionCommand() {
        super("version", "show the current cloud version");
    }

    @Override
    public void execute(String[] args) {
        System.out.println("ByteCloud version "+byteCloud.getVersion());
    }
}
