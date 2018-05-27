package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;

/**
 * Created by ByteList on 27.05.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DebugCommand extends Command {

    public DebugCommand() {
        super("debug", "enable/disable the debug mode");
    }

    @Override
    public void execute(String[] args) {
        ByteCloud.getInstance().setDebug(!ByteCloud.getInstance().isDebug());
        System.out.println("Debug-Mode: "+ByteCloud.getInstance().isDebug()+".");
    }
}
