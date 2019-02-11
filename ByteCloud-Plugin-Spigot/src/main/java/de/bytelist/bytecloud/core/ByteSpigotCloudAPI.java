package de.bytelist.bytecloud.core;

import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.spigot.SpigotCloudAPI;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.packet.server.PacketInChangeServerState;
import de.bytelist.bytecloud.packet.server.PacketInKickPlayer;
import de.bytelist.bytecloud.packet.server.PacketInStopOwnServer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteSpigotCloudAPI implements SpigotCloudAPI {

    @Override
    public String getCurrentServerId() {
        return ByteCloudCore.getInstance().getCloudHandler().getServerId();
    }

    @Override
    public void changeServerState(ServerState serverState) {
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(getCurrentServerId(), DatabaseServerObject.STATE,
                serverState.toString());
        ByteCloudCore.getInstance().getPacketClient().sendPacket(new PacketInChangeServerState(getCurrentServerId(), serverState.name()));
    }

    @Override
    public void setMotd(String motd) {
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(getCurrentServerId(), DatabaseServerObject.MOTD, motd);
    }

    @Override
    public void shutdown() {
        PacketInStopOwnServer packetInStopOwnServer = new PacketInStopOwnServer(getCurrentServerId());
        ByteCloudCore.getInstance().getPacketClient().sendPacket(packetInStopOwnServer);
    }

    @Override
    public CloudPlayer<Player> getPlayer(UUID uuid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CloudPlayer<Player> getPlayer(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Logger getLogger() {
        return ByteCloudCore.getInstance().getLogger();
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
    public String getRandomLobbyId() {
        return ByteCloudCore.getInstance().getCloudHandler().getRandomLobbyId();
    }

    @Override
    public String getRandomLobbyId(String excludedLobby) {
        return ByteCloudCore.getInstance().getCloudHandler().getRandomLobbyId(excludedLobby);
    }

    @Override
    public void movePlayerToLobby(UUID uuid) {
        this.movePlayerToServer(uuid, this.getRandomLobbyId());
    }

    @Override
    public void movePlayerToServer(UUID uuid, String serverId) {
        Player player = Bukkit.getPlayer(uuid);

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
    public void kickPlayer(String playerName, String reason) {
        ByteCloudCore.getInstance().getPacketClient().sendPacket(new PacketInKickPlayer(playerName, reason));
    }

    @Override
    public String getServerIdFromPlayer(UUID uuid) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UUID getUniqueIdFromName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
