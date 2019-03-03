package de.bytelist.bytecloud.core.cloud;

import de.bytelist.bytecloud.CloudAPIHandler;
import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerKickPacket;
import de.bytelist.bytecloud.core.ByteCloudCore;
import de.bytelist.bytecloud.core.event.ByteCloudPlayerConnectToServerEvent;
import de.bytelist.bytecloud.core.event.ByteCloudServerUpdateEvent;
import de.bytelist.bytecloud.core.event.ByteCloudServerUpdateStateEvent;
import de.bytelist.bytecloud.database.DatabaseElement;
import de.bytelist.bytecloud.database.DatabaseManager;
import de.bytelist.bytecloud.database.DatabaseServer;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 20.12.2016.
 */
public class CloudHandler extends CloudAPIHandler {

    private final ByteCloudCore byteCloudCore = ByteCloudCore.getInstance();

    private DatabaseServer databaseServer;
    @Getter
    private String serverId, serverGroup;

    @Getter @Setter
    private String cloudVersion, cloudStarted;
    @Getter @Setter
    private boolean cloudRunning;

    public CloudHandler() {
        String host = byteCloudCore.getCloudConfig().getString("mongo-host");
        String database = byteCloudCore.getCloudConfig().getString("mongo-database");
        String user = byteCloudCore.getCloudConfig().getString("mongo-user");
        String password = byteCloudCore.getCloudConfig().getString("mongo-password");

        this.serverId = System.getProperty("de.bytelist.bytecloud.servername", Bukkit.getServerName());
        this.serverGroup = System.getProperty("de.bytelist.bytecloud.servergroup", "null");

        DatabaseManager databaseManager = new DatabaseManager(host, 27017, user, password, database);
        Bukkit.getConsoleSender().sendMessage(Cloud.PREFIX+"§eDatabase - §aConnected!");
        this.databaseServer = databaseManager.getDatabaseServer();
    }

    @Override
    public void kickCloudPlayer(CloudPlayerKickPacket cloudPlayerKickPacket) {
        Player player = Bukkit.getPlayer(cloudPlayerKickPacket.getUuid());

        if(player != null)
            Bukkit.getScheduler().runTaskLater(ByteCloudCore.getInstance(), ()-> player.kickPlayer(cloudPlayerKickPacket.getReason()), 2);
    }

    public Integer getSocketPort() {
        return byteCloudCore.getCloudConfig().getInt("socket-port");
    }

    public String getRandomLobbyId() {
        List<String> lobbyServer = new ArrayList<>(getServerInDatabase("Lobby"));

        int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

        return lobbyServer.get(i);
    }

    public String getRandomLobbyId(String excludedLobbyId) {
        List<String> lobbyServer = new ArrayList<>();
        for(String lb : getServerInDatabase("Lobby"))
            if(!lb.equals(excludedLobbyId)) lobbyServer.add(lb);

        int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

        return lobbyServer.get(i);
    }

    public String getUniqueServerId(String serverName) {
        return ServerIdResolver.getUniqueServerId(serverName);
    }

    public void callCloudServerUpdateEvent(String serverId, String serverGroup) {
        Bukkit.getPluginManager().callEvent(new ByteCloudServerUpdateEvent(serverId, serverGroup));
    }

    public void callCloudServerUpdateStateEvent(String serverId, String serverGroup, String oldState, String newState) {
        Bukkit.getPluginManager().callEvent(new ByteCloudServerUpdateStateEvent(serverId, serverGroup, ServerState.valueOf(oldState), ServerState.valueOf(newState)));
    }

    public void callCloudPlayerConnectToServerEvent(String player, String oldServer, String oldServerGroup, String targetServer, String targetServerGroup) {
        if(oldServer.equals("null")) {
            oldServer = null;
            oldServerGroup = null;
        }

        Bukkit.getPluginManager().callEvent(new ByteCloudPlayerConnectToServerEvent(player, oldServer, oldServerGroup, targetServer, targetServerGroup));
    }
}
