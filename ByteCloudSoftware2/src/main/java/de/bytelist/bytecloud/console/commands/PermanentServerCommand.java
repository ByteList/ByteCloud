package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.server.PermServer;
import de.bytelist.bytecloud.server.PermServerObject;
import de.bytelist.bytecloud.server.Server;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PermanentServerCommand extends Command {

    public PermanentServerCommand() {
        super("pserver", "permanent server commands");
    }

    private final Logger logger = ByteCloud.getInstance().getLogger();

    @Override
    public void execute(String[] args) {

        if(args.length == 7) {
            if(args[0].equalsIgnoreCase("create")) {
                String name = args[1];
                int player = Integer.parseInt(args[2]);
                int spectator = Integer.parseInt(args[3]);
                int ramM = Integer.parseInt(args[4]);
                int port = Integer.parseInt(args[5]);
                boolean autoStart = Boolean.parseBoolean(args[6]);

                File dir = new File(EnumFile.SERVERS_PERMANENT.getPath(), name);
                if(!dir.exists()) dir.mkdirs();
                File file = new File(dir, "settings.bci");
                if (!file.exists())
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                ArrayList<String> lines = new ArrayList<>();
                lines.add("player=" + player);
                lines.add("spectator=" + spectator);
                lines.add("ram=" + ramM);
                lines.add("port=" + port);
                lines.add("autoStart=" + autoStart);

                try {
                    FileUtils.writeLines(file, lines);
                    logger.info("Permanent server " + name + " created!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("info")) {
                String name = args[1];
                File dir = new File(EnumFile.SERVERS_PERMANENT.getPath(), name);
                File file = new File(dir, "settings.bci");

                if (!file.exists()) {
                    logger.info("Can not find settings.bci for permanent server " + name+"!");
                } else {
                    logger.info("Permanent server information for " + name + ":");
                    try {
                        ArrayList<Object> lines = new ArrayList<Object>(FileUtils.readLines(file));
                        for (Object l : lines) {
                            logger.info(l.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            if(args[0].equalsIgnoreCase("delete")) {
                String name = args[1];
                File file = new File(EnumFile.SERVERS_PERMANENT.getPath(), name);

                if (!file.exists()) {
                    logger.info("Can not find permanent server directory for " + name);
                } else {
                    try {
                        FileUtils.deleteDirectory(file);
                        logger.info("Permanent server " + name + " deleted!");
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            if(args[0].equalsIgnoreCase("start")) {
                String serverName = args[1];
                Server server = ByteCloud.getInstance().getServerHandler().getServer(serverName);

                if(server != null && server instanceof PermServer) {
                    PermServer permServer = (PermServer) server;

                    if (permServer.isRunning()) {
                        logger.info("Permanent server " + serverName + " is already running!");
                    } else {
                        PermServerObject permServerObject = permServer.getPermServerObject();
                        permServer.startServer("_cloud",
                                permServerObject.get("ram").getAsInt(),
                                permServerObject.get("player").getAsInt(),
                                permServerObject.get("spectator").getAsInt());
                    }
                } else if(ByteCloud.getInstance().getServerHandler().existsPermanentServer(serverName)) {
                    final PermServerObject permServerObject = new PermServerObject(serverName);
                    PermServer permServer = new PermServer(serverName, permServerObject);
                    permServer.startServer("_cloud",
                            permServerObject.get("ram").getAsInt(),
                            permServerObject.get("player").getAsInt(),
                            permServerObject.get("spectator").getAsInt());
                } else {
                    logger.info(serverName+" isn't a permanent server!");
                }
                return;
            }
            if(args[0].equalsIgnoreCase("stop")) {
                String serverName = args[1];
                Server server = ByteCloud.getInstance().getServerHandler().getServer(serverName);

                if(server != null && server instanceof PermServer) {
                    ((PermServer)server).stopServer("_cloud", Server.StopType.MOVE_TO_LOBBY);
                } else {
                    logger.info(serverName+" isn't a permanent server!");
                }
                return;
            }
        }


        System.out.println("Using perm server command:");
        System.out.println("pserver create <name> <player> <spectator> <ram> <port> <autoStart>");
        System.out.println("pserver delete <name>");
        System.out.println("pserver info <name>");
        System.out.println("pserver start <name>");
        System.out.println("pserver stop <name>");
    }
}
