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
        super("cloud", "cloud commands");
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
                    logger.info("This cloud group does not exist.");
                }
                return;
            }
            if (args[0].equalsIgnoreCase("stop")) {
                String serverName = args[1];
                Server server = byteCloud.getServerHandler().getServer(serverName);

                if (server != null) {
                    server.stopServer("_cloud");
                } else {
                    logger.info("The cloud "+serverName+" doesn't exist!");
                }
                return;
            }

            if(args[0].equalsIgnoreCase("screen")) {
                String serverName = args[1];
                Server server = byteCloud.getServerHandler().getServer(serverName);

                if(server != null) {
                    byteCloud.getScreenManager().joinNewScreen(server);
                    return;
                }

                logger.info("Could not find a cloud with id: "+serverName);
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

        System.out.println("Using cloud command:");
        System.out.println("cloud start <group>");
        System.out.println("cloud stop <id>");
        System.out.println("cloud screen <id>");
        System.out.println("cloud list");
    }
}
