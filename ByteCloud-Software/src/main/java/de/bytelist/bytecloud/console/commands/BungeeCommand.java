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
                    bungee.stopBungee(()-> bungee.startBungee(()-> byteCloud.getLogger().info("Bungee restarted.")), ()-> {});
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
                    bungee.startBungee(()-> {});
                    return;
                } else {
                    byteCloud.getLogger().info("Bungee is already running!");
                    return;
                }
            }
            if(args[0].equalsIgnoreCase("screen")) {
                if(bungee.isRunning()) {
                    byteCloud.getScreenManager().joinNewScreen(bungee);
                    return;
                } else {
                    byteCloud.getLogger().info("Bungee isn't running!");
                    return;
                }
            }
        }
        if(args.length > 1 && args[0].equals("exec")) {
            StringBuilder cmd = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                cmd.append(args[i]).append(" ");
            }
//            PacketOutExecuteCommand packetOutExecuteCommand = new PacketOutExecuteCommand(cmd.toString());
//            byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutExecuteCommand);
            byteCloud.getLogger().info("Executed cmd ("+byteCloud.getBungee().getBungeeId()+"): "+cmd);
            return;
        }
        byteCloud.getLogger().info("bungee Commands: \n"+
                "   bungee restart - Restarting the bungee.\n"+
                "   bungee stop - Stopping the bungee.\n"+
                "   bungee start - Starting the bungee.\n"+
                "   bungee screen - Screen in the bungee.\n"+
                "   bungee exec <command> - Execute a command on the bungee.\n");
    }
}
