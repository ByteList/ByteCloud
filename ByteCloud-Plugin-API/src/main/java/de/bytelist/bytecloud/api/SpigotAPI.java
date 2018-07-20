package de.bytelist.bytecloud.api;

import org.bukkit.entity.Player;

/**
 * Created by ByteList on 20.07.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface SpigotAPI extends CloudAPI {

    public String getCurrentServerId();

    public void changeServerState(ServerState serverState);

    public void setMotd(String motd);

    public void shutdown();


    @Deprecated
    public void addPlayer(Player player);

    public void addPlayer(String player);

    @Deprecated
    public void removePlayer(Player player);

    public void removePlayer(String player);

    @Deprecated
    public void addSpectator(Player player);

    public void addSpectator(String player);

    @Deprecated
    public void removeSpectator(Player player);

    public void removeSpectator(String player);

    public void movePlayerToLobby(Player player);

    public void movePlayerToServer(Player player, String serverId);
}
