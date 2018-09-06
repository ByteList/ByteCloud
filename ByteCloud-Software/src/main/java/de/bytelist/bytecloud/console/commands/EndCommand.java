package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.updater.Updater;

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

        if(args.length == 1 && args[0].equalsIgnoreCase("-update")) {
            new Updater();
        }
        ByteCloud.getInstance().stop();
    }
}
