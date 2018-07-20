package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.api.BungeeAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BungeeCloudAPI implements BungeeAPI {


    @Override
    public void movePlayerToLobby(ProxiedPlayer player) {
        this.movePlayerToLobby(player.getUniqueId());
    }

    @Override
    public void movePlayerToServer(ProxiedPlayer player, String serverId) {
        player.connect(ByteCloudMaster.getInstance().getProxy().getServerInfo(serverId));
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
    public void movePlayerToLobby(UUID uuid) {
        this.movePlayerToServer(uuid, ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId());
    }

    @Override
    public void movePlayerToServer(UUID uuid, String serverId) {
        this.movePlayerToServer(ByteCloudMaster.getInstance().getProxy().getPlayer(uuid), serverId);
    }

    @Override
    public void kickPlayer(String playerName, String reason) {
        ProxiedPlayer player = ByteCloudMaster.getInstance().getProxy().getPlayer(playerName);
        player.disconnect(reason);
    }
}
