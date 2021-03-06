package de.bytelist.bytecloud.core;

import de.bytelist.bytecloud.CloudAPIHandler;
import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.common.CloudLocation;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.packet.client.player.ClientPlayerLocationPacket;
import de.bytelist.bytecloud.common.packet.client.server.ClientServerChangeStatePacket;
import de.bytelist.bytecloud.common.packet.client.server.ClientServerSetMotdPacket;
import de.bytelist.bytecloud.common.packet.client.server.ClientServerStopPacket;
import de.bytelist.bytecloud.common.packet.client.player.ClientPlayerKickPacket;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.bytelist.bytecloud.common.server.CloudServerGroup;
import de.bytelist.bytecloud.common.spigot.SpigotCloudAPI;
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
        ByteCloudCore.getInstance().getSession().send(new ClientServerChangeStatePacket(getCurrentServerId(), serverState));
    }

    @Override
    public void setMotd(String motd) {
        ByteCloudCore.getInstance().getSession().send(new ClientServerSetMotdPacket(getCurrentServerId(), motd));
    }

    @Override
    public void shutdown(UUID sender) {
        ByteCloudCore.getInstance().getSession().send(new ClientServerStopPacket(this.getCurrentServerId(), sender));
    }

    @Override
    public void setLocation(UUID uuid, CloudLocation cloudLocation) {
        ByteCloudCore.getInstance().getSession().send(new ClientPlayerLocationPacket(uuid, cloudLocation,
                false, this.getCurrentServerId()));
    }

    @Override
    public Collection<CloudServerGroup> getServerGroups() {
        return Collections.unmodifiableCollection(ByteCloudCore.getInstance().getCloudHandler().getCloudServerGroups().values());
    }

    @Override
    public Collection<CloudServer> getServers() {
        return Collections.unmodifiableCollection(ByteCloudCore.getInstance().getCloudHandler().getCloudServers().values());
    }

    @Override
    public Collection<CloudPlayer> getPlayers() {
        return Collections.unmodifiableCollection(ByteCloudCore.getInstance().getCloudHandler().getCloudPlayers());
    }

    @Override
    public CloudServerGroup getServerGroup(String name) {
        return CloudAPIHandler.getInstance().getCloudServerGroups().get(name);
    }

    @Override
    public CloudServer getServer(String serverId) {
        return CloudAPIHandler.getInstance().getCloudServer(serverId);
    }

    @Override
    public CloudPlayer getPlayer(UUID uuid) {
        return CloudAPIHandler.getInstance().getCloudPlayer(uuid);
    }

    @Override
    public CloudPlayer getPlayer(String name) {
        return CloudAPIHandler.getInstance().getCloudPlayer(name);
    }

    @Override
    public Logger getLogger() {
        return ByteCloudCore.getInstance().getLogger();
    }


    @Override
    public String getUniqueServerId(String server) {
        return ServerIdResolver.getUniqueServerId(server, ByteCloudCore.getInstance().getCloudHandler().getCloudServers().keySet());
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
    public void movePlayerToServerAndTeleport(UUID uuid, String serverId, CloudLocation cloudLocation) {
        this.movePlayerToServer(uuid, serverId);
        ByteCloudCore.getInstance().getSession().send(new ClientPlayerLocationPacket(uuid, cloudLocation,
                true, serverId));
    }

    @Override
    public void kickPlayer(UUID uuid, String reason) {
        ByteCloudCore.getInstance().getSession().send(new ClientPlayerKickPacket(uuid, reason));
    }

    @Override
    public String getServerIdFromPlayer(UUID uuid) {
        return this.getPlayer(uuid).getCurrentServer().getServerId();
    }

    @Override
    public UUID getUniqueIdFromName(String name) {
        CloudPlayer cloudPlayer = this.getPlayer(name);

        if(cloudPlayer != null)
            return cloudPlayer.getUuid();

        return null;
    }
}
