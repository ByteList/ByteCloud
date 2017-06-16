package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class EndCommand extends Command {

    public EndCommand() {
        super("end", "shutdown the cloud");
    }

    @Override
    public void execute(String[] args) {
        ByteCloud.getInstance().stop();
    }
}
