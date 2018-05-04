package de.bytelist.bytecloud.bungee.cloud;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.database.DatabaseManager;
import de.bytelist.bytecloud.database.DatabaseServer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 28.01.2017.
 */
public class CloudHandler {

    private final ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();

    @Getter
    private DatabaseServer databaseServer;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private final String bungeeId;

    @Getter @Setter
    private String cloudVersion, cloudStarted;
    @Getter @Setter
    private boolean cloudRunning;

    public CloudHandler() {
        String host = byteCloudMaster.getConfig().getString("mongo-host");
        String database = byteCloudMaster.getConfig().getString("mongo-database");
        String user = byteCloudMaster.getConfig().getString("mongo-user");
        String password = byteCloudMaster.getConfig().getString("mongo-password");

        this.bungeeId = System.getProperty("de.bytelist.bytecloud.servername", "Bungee-0");

        try {
            this.databaseManager = new DatabaseManager(host, 27017, user, password, database);
            ByteCloudMaster.getInstance().getProxy().getConsole().sendMessage(ByteCloudMaster.getInstance().prefix+"§eDatabase - §aConnected!");
            this.databaseServer = this.databaseManager.getDatabaseServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Integer getSocketPort() {
        return byteCloudMaster.getConfig().getInt("socket-port");
    }

    public String getCloudAddress() {
        return "127.0.0.1";
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

    public String getRandomLobbyId() {
        List<String> lobbyServer = new ArrayList<>();
//        lobbyServer.addAll(getServerInDatabase("LOBBY"));

        for(String server : ByteCloudMaster.getInstance().getProxy().getServers().keySet()) {
            if(server.startsWith("lb")) {
                lobbyServer.add(server);
            }
        }
        if(!lobbyServer.isEmpty()) {
            int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

            return lobbyServer.get(i);
        }
        return null;
    }

    public String getRandomLobbyId(String excludedLobbyId) {
        List<String> lobbyServer = new ArrayList<>();
//        for(String lb : getServerInDatabase("LOBBY"))
//            if(!lb.equals(excludedLobbyId)) lobbyServer.add(lb);

        for(String server : ByteCloudMaster.getInstance().getProxy().getServers().keySet()) {
            if(server.startsWith("lb") && !server.equals(excludedLobbyId)) {
                lobbyServer.add(server);
            }
        }

        int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

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

    public int connect(String server, ProxiedPlayer proxiedPlayer) {
        ServerInfo serverInfo = ByteCloudMaster.getInstance().getProxy().getServerInfo(server);

        if (serverInfo == null)
            return 2;

        if (proxiedPlayer.getServer().getInfo() == serverInfo)
            return 1;

        proxiedPlayer.connect(serverInfo);
        return 0;
    }

    public int move(ProxiedPlayer proxiedPlayer, ProxiedPlayer target) {
        if (target == null)
            return 2;

        ServerInfo serverInfo = target.getServer().getInfo();

        if (proxiedPlayer.getServer().getInfo() == serverInfo)
            return 1;

        proxiedPlayer.connect(serverInfo);
        return 0;
    }
}
