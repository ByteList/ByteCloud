package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.bungee.Bungee;
import de.bytelist.bytecloud.console.Command;

/**
 * Created by ByteList on 24.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BungeeCommand extends Command {

    public BungeeCommand() {
        super("bungee", "Simple commands for the bungee");
    }

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    @Override
    public void execute(String[] args) {
        if(args.length == 1) {
            Bungee bungee = byteCloud.getBungee();
            if(args[0].equalsIgnoreCase("restart")) {
                byteCloud.getLogger().info("Restarting bungee...");
                if(bungee.isRunning()) {
                    bungee.execByCommand = true;
                    bungee.stopBungee();
                    try {
                        Thread.sleep(23000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bungee.startBungee();
                    return;
                } else {
                    byteCloud.getLogger().info("Bungee isn't running!");
                    return;
                }
            }
            if(args[0].equalsIgnoreCase("stop")) {
                if(bungee.isRunning()) {
                    bungee.stopBungee();
                    return;
                } else {
                    byteCloud.getLogger().info("Bungee isn't running!");
                    return;
                }
            }
            if(args[0].equalsIgnoreCase("start")) {
                if(!bungee.isRunning()) {
                    bungee.execByCommand = true;
                    bungee.startBungee();
                    return;
                } else {
                    byteCloud.getLogger().info("Bungee is already running!");
                    return;
                }
            }
        }
        byteCloud.getLogger().info("bungee Commands: \n"+
                "   bungee restart - Restarting the bungee.\n"+
                "   bungee stop - Stopping the bungee.\n"+
                "   bungee start - Starting the bungee.\n");
    }
}
