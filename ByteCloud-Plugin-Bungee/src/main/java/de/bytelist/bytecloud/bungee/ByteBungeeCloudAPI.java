package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.CloudAPIHandler;
import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.common.CloudLocation;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.bungee.BungeeCloudAPI;
import de.bytelist.bytecloud.common.packet.client.player.ClientPlayerLocationPacket;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.bytelist.bytecloud.common.server.CloudServerGroup;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteBungeeCloudAPI implements BungeeCloudAPI {

    @Override
    public Collection<CloudServerGroup> getServerGroups() {
        return Collections.unmodifiableCollection(ByteCloudMaster.getInstance().getCloudHandler().getCloudServerGroups().values());
    }

    @Override
    public Collection<CloudServer> getServers() {
        return Collections.unmodifiableCollection(ByteCloudMaster.getInstance().getCloudHandler().getCloudServers().values());
    }

    @Override
    public Collection<CloudPlayer> getPlayers() {
        return Collections.unmodifiableCollection(ByteCloudMaster.getInstance().getCloudHandler().getCloudPlayers());
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
        return ByteCloudMaster.getInstance().getLogger();
    }

    @Override
    public String getUniqueServerId(String server) {
        return ServerIdResolver.getUniqueServerId(server, ByteCloudMaster.getInstance().getCloudHandler().getCloudServers().keySet());
    }

    @Override
    public String getRandomLobbyId() {
        return ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId();
    }

    @Override
    public String getRandomLobbyId(String excludedLobby) {
        return ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId(excludedLobby);
    }

    @Override
    public void movePlayerToLobby(UUID uuid) {
        this.movePlayerToServer(uuid, this.getRandomLobbyId());
    }

    @Override
    public void movePlayerToServer(UUID uuid, String serverId) {
        ByteCloudMaster.getInstance().getProxy().getPlayer(uuid).connect(ByteCloudMaster.getInstance().getProxy().getServerInfo(serverId));
    }

    @Override
    public void movePlayerToServerAndTeleport(UUID uuid, String serverId, CloudLocation cloudLocation) {
        ByteCloudMaster.getInstance().getProxy().getPlayer(uuid).connect(ByteCloudMaster.getInstance().getProxy().getServerInfo(serverId));
        ByteCloudMaster.getInstance().getSession().send(new ClientPlayerLocationPacket(uuid, cloudLocation,
                true, serverId));
    }

    @Override
    public void kickPlayer(UUID uuid, String reason) {
        ProxiedPlayer player = ByteCloudMaster.getInstance().getProxy().getPlayer(uuid);
        player.disconnect(reason);
    }

    @Override
    public String getServerIdFromPlayer(UUID uuid) {
        return this.getPlayer(uuid).getCurrentServer().getServerId();
    }

    @Override
    public UUID getUniqueIdFromName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
