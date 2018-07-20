package de.bytelist.bytecloud.core;

import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.api.ServerState;
import de.bytelist.bytecloud.api.SpigotAPI;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.network.server.PacketInChangeServerState;
import de.bytelist.bytecloud.network.server.PacketInKickPlayer;
import de.bytelist.bytecloud.network.server.PacketInStopOwnServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class SpigotCloudAPI implements SpigotAPI {

    @Override
    public String getCurrentServerId() {
        return ByteCloudCore.getInstance().getCloudHandler().getServerId();
    }

    @Override
    public void changeServerState(ServerState serverState) {
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(getCurrentServerId(), DatabaseServerObject.STATE,
                serverState.toString());
        ByteCloudCore.getInstance().getServerClient().sendPacket(new PacketInChangeServerState(getCurrentServerId(), serverState.name()));
    }

    @Override
    public void setMotd(String motd) {
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(getCurrentServerId(), DatabaseServerObject.MOTD, motd);
    }

    @Override
    public void shutdown() {
        PacketInStopOwnServer packetInStopOwnServer = new PacketInStopOwnServer(getCurrentServerId());
        ByteCloudCore.getInstance().getServerClient().sendPacket(packetInStopOwnServer);
    }

    @Override
    @Deprecated
    public void addPlayer(Player player) {
        this.addPlayer(player.getName());
    }

    @Override
    public void addPlayer(String player) {
        String serverId = getCurrentServerId();
        Integer value = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt()+1;
        String connectedPlayer = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.PLAYERS).getAsString();

        connectedPlayer = connectedPlayer+(player+",");

        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.PLAYER_ONLINE, value);
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.PLAYERS, connectedPlayer);
    }

    @Override
    @Deprecated
    public void removePlayer(Player player) {
        this.removePlayer(player.getName());
    }

    @Override
    public void removePlayer(String player) {
        String serverId = getCurrentServerId();
        Integer value = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt()-1;
        String connectedPlayer =  ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.PLAYERS).getAsString();

        connectedPlayer = connectedPlayer.replace(player+",", "");

        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.PLAYER_ONLINE, value);
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.PLAYERS, connectedPlayer);
    }

    @Override
    @Deprecated
    public void addSpectator(Player player) {
        this.addSpectator(player.getName());
    }

    @Override
    public void addSpectator(String player) {
        String serverId = getCurrentServerId();
        Integer value = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt()+1;
        String connectedPlayer = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.SPECTATORS).getAsString();

        connectedPlayer = connectedPlayer+(player+",");

        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.SPECTATOR_ONLINE, value);
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.SPECTATORS, connectedPlayer);
    }

    @Override
    @Deprecated
    public void removeSpectator(Player player) {
        this.removeSpectator(player.getName());
    }

    @Override
    public void removeSpectator(String player) {
        String serverId = getCurrentServerId();
        Integer value = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt()-1;
        String connectedPlayer = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.SPECTATORS).getAsString();

        connectedPlayer = connectedPlayer.replace(player+",", "");

        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.SPECTATOR_ONLINE, value);
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.SPECTATORS, connectedPlayer);
    }

    @Override
    public void movePlayerToLobby(Player player) {
        this.movePlayerToServer(player, ByteCloudCore.getInstance().getCloudHandler().getRandomLobbyId());
    }

    @Override
    public void movePlayerToServer(Player player, String serverId) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(serverId);
            player.sendPluginMessage(ByteCloudCore.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<String> getServers() {
        return Collections.unmodifiableCollection(ByteCloudCore.getInstance().getCloudHandler().getServerInDatabase());
    }

    @Override
    public String getUniqueServerId(String server) {
        return ServerIdResolver.getUniqueServerId(server);
    }

    @Override
    public void movePlayerToLobby(UUID uuid) {
        this.movePlayerToLobby(Bukkit.getPlayer(uuid));
    }

    @Override
    public void movePlayerToServer(UUID uuid, String serverId) {
        this.movePlayerToServer(Bukkit.getPlayer(uuid), serverId);
    }

    @Override
    public void kickPlayer(String playerName, String reason) {
        ByteCloudCore.getInstance().getServerClient().sendPacket(new PacketInKickPlayer(playerName, reason));
    }
}
