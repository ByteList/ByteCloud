package de.bytelist.bytecloud;

import de.bytelist.bytecloud.database.DatabaseManager;
import de.bytelist.bytecloud.database.DatabaseServer;
import de.bytelist.bytecloud.database.DatabaseServerObject;

import java.util.Collection;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerIdResolver {

    /**
     * Try to solve a server id to return the complete unique server id.
     * <br>
     * For example,<code>lb-1</code> is given. The method looks for it at the database to return the unique id.
     *
     * @param serverId to solve.
     * @return the unique id from the solved id.
     */
    public static String getUniqueServerId(String serverId) {
        String[] args = serverId.split("-");

        if(DatabaseManager.getInstance() == null) throw new NullPointerException("Database manager is not initialised!");
        DatabaseServer databaseServer = DatabaseManager.getInstance().getDatabaseServer();

        if(args.length == 1 && databaseServer.existsServer(serverId)) {
            return databaseServer.getDatabaseElement(serverId, DatabaseServerObject.SERVER_ID).getAsString();
        }

        return databaseServer.getServer().stream().filter(server -> server.startsWith(serverId)).findFirst().orElse(null);
    }

    /**
     * Try to solve a server id to return the complete unique server id.
     * <br>
     * For example,<code>lb-1</code> is given. The method looks for it at the given servers collection to return the unique id.
     *
     * @param serverId to solve.
     * @param servers to look for the server id.
     * @return the unique id from the solved id.
     */
    public static String getUniqueServerId(String serverId, Collection<String> servers) {
        String[] args = serverId.split("-");

        if(args.length == 1 && servers.contains(serverId)) {
            return serverId;
        }

        return servers.stream().filter(server -> server.startsWith(serverId)).findFirst().orElse(null);
    }
}