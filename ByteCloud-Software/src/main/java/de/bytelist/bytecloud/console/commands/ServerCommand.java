package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.server.Server;
import de.bytelist.bytecloud.server.ServerGroup;

import java.util.logging.Logger;

/**
 * Created by ByteList on 09.08.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerCommand extends Command {

    public ServerCommand() {
        super("server", "server commands");
    }

    private final ByteCloud byteCloud = ByteCloud.getInstance();
    private final Logger logger = byteCloud.getLogger();

    @Override
    public void execute(String[] args) {
        if(args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {

                String group = args[1];

                ServerGroup serverGroup = null;
                for (ServerGroup sg : byteCloud.getServerHandler().getServerGroups().values()) {
                    if (sg.getGroupName().equalsIgnoreCase(group)) {
                        serverGroup = sg;
                        break;
                    }
                }
                if(serverGroup != null) {
                    serverGroup.startNewServer("_cloud");
                } else {
                    logger.info("This server group does not exist.");
                }
                return;
            }
            if (args[0].equalsIgnoreCase("stop")) {
                String serverName = args[1];
                Server server = byteCloud.getServerHandler().getServer(serverName);

                if (server != null) {
                    server.stopServer("_cloud");
                } else {
                    logger.info("The server "+serverName+" doesn't exist!");
                }
                return;
            }

            if(args[0].equalsIgnoreCase("screen")) {
                String serverName = args[1];
                Server server = byteCloud.getServerHandler().getServer(serverName);

                if(server != null) {
                    byteCloud.getScreenSystem().joinNewScreen(server);
                    try {
                        Thread.sleep(500L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    byteCloud.getLogger().info("** You are now in the screen session [" + server.getServerId() + "]");
                    byteCloud.getLogger().info("** You can leave it with the command \"screen leave\"");
                } else {
                    logger.info("The server "+serverName+" doesn't exist!");
                }
                return;
            }
        }

        if(args.length == 1 && args[0].equalsIgnoreCase("list")) {

            logger.info("Server-List:");
            for(Server server : byteCloud.getServerHandler().getServers()) {
                logger.info(server.getServerId()+" - Port: "+server.getPort());
            }
            return;
        }

        System.out.println("Using server command:");
        System.out.println("server start <group>");
        System.out.println("server stop <id>");
        System.out.println("server screen <id>");
        System.out.println("server list");
    }
}
