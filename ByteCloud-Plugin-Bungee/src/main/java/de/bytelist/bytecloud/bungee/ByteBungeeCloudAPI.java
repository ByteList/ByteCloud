package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.common.bungee.BungeeCloudAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteBungeeCloudAPI implements BungeeCloudAPI {

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
}
