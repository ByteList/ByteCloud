package de.bytelist.bytecloud.core.cloud;

import de.bytelist.bytecloud.core.ByteCloudCore;
import de.bytelist.bytecloud.database.DatabaseElement;
import de.bytelist.bytecloud.database.DatabaseManager;
import de.bytelist.bytecloud.database.DatabaseServer;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.core.properties.CloudProperties;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ByteList on 20.12.2016.
 */
public class CloudHandler {

    private DatabaseServer databaseServer;
    private DatabaseManager databaseManager;
    @Getter
    private String serverId;

    @Getter @Setter
    private String cloudVersion, cloudStarted;
    @Getter @Setter
    private boolean cloudRunning;

    public CloudHandler() {
        String host = CloudProperties.getCloudProperties().getProperty("mongo-host");
        String database = CloudProperties.getCloudProperties().getProperty("mongo-database");
        String user = CloudProperties.getCloudProperties().getProperty("mongo-user");
        String password = CloudProperties.getCloudProperties().getProperty("mongo-password");

        this.serverId = System.getProperty("de.bytelist.bytecloud.servername", Bukkit.getServerName());

        try {
            this.databaseManager = new DatabaseManager(host, 27017, user, password, database);
            Bukkit.getConsoleSender().sendMessage(ByteCloudCore.getInstance().prefix+"§eDatabase - §aConnected!");
            this.databaseServer = this.databaseManager.getDatabaseServer();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Integer getSocketPort() {
        return Integer.valueOf(CloudProperties.getCloudProperties().getProperty("socket-port"));
    }

    /**
     * Basically you can check if a server exists.
     *
     * @param id
     * @return boolean
     */
    public boolean existsServerInDatabase(String id) {
        return this.databaseServer.existsServer(id);
    }

    /**
     * With this method you can edit a value in the database.
     *
     * @param value
     */
    public void editDatabaseServerValue(String id, DatabaseServerObject databaseServerObject, Object value) {
        this.databaseServer.setDatabaseObject(id, databaseServerObject, value);
    }

    /**
     * Gets a value from a server where your value.
     *
     * @return an Object from the server.
     */
    public DatabaseElement getDatabaseServerValue(String id, DatabaseServerObject databaseServerObject) {
        return this.databaseServer.getDatabaseElement(id, databaseServerObject);
    }

    /**
     * Gets all Server in a String-List.
     *
     * @return List<String> with all server's in database
     */
    public List<String> getServerInDatabase() {
        return this.databaseServer.getServer();
    }

    public List<String> getServerInDatabase(String type) {
        return this.databaseServer.getServer(type);
    }

    /**
     * Delete a server where your value.
     *
     */
    public void removeServerFromDatabase(String id) {
        this.databaseServer.removeServer(id);
    }

    public String getRandomLobbyId() {
        List<String> lobbyServer = new ArrayList<>();
        for (String server : getServerInDatabase("LOBBY")) {
            lobbyServer.add(server);
        }

        Random random = new Random();
        int i = random.nextInt(lobbyServer.size());

        return lobbyServer.get(i);
    }

    public String getUniqueServerId(String serverName) {
        String uid = null;

        for (String id : getServerInDatabase())
            if (id.contains(serverName)) {
                uid = id;
            }

        return uid;
    }

}
