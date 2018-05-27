package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;

/**
 * Created by ByteList on 27.05.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DebugCommand extends Command {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public DebugCommand() {
        super("debug", "enable/disable the debug mode");
    }

    @Override
    public void execute(String[] args) {

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("queue")) {
                byteCloud.getCloudExecutor().setExtendedDebug(!byteCloud.getCloudExecutor().isExtendedDebug());
                System.out.println("Debug-Mode: (queue) "+byteCloud.getCloudExecutor().isExtendedDebug()+".");
                return;
            }
        }
        byteCloud.setDebug(!byteCloud.isDebug());
        System.out.println("Debug-Mode: "+byteCloud.isDebug()+".");
    }
}
