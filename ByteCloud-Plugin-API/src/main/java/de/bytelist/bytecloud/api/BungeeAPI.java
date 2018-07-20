package de.bytelist.bytecloud.api;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface BungeeAPI extends CloudAPI {

    public void movePlayerToLobby(ProxiedPlayer player);

    public void movePlayerToServer(ProxiedPlayer player, String serverId);
}
