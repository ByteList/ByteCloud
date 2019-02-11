package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.bungee.BungeeCloudAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteBungeeCloudAPI implements BungeeCloudAPI {

    @Override
    public Logger getLogger() {
        return ByteCloudMaster.getInstance().getLogger();
    }

    @Override
    public Collection<String> getServers() {
        return ByteCloudMaster.getInstance().getCloudHandler().getServerInDatabase();
    }

    @Override
    public String getUniqueServerId(String server) {
        return ServerIdResolver.getUniqueServerId(server);
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
    public void kickPlayer(String playerName, String reason) {
        ProxiedPlayer player = ByteCloudMaster.getInstance().getProxy().getPlayer(playerName);
        player.disconnect(reason);
    }

    @Override
    public String getServerIdFromPlayer(UUID uuid) {
        return null;
    }

    @Override
    public UUID getUniqueIdFromName(String name) {
        return null;
    }

    @Override
    public CloudPlayer<ProxiedPlayer> getPlayer(UUID uuid) {
        return null;
    }

    @Override
    public CloudPlayer<ProxiedPlayer> getPlayer(String name) {
        return null;
    }
}
