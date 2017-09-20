package de.bytelist.bytecloud.core.event;

import de.bytelist.bytecloud.core.cloud.CloudAPI;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 29.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class AsyncLobbyUpdateStateEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private String serverId;
    @Getter
    private String serverGroup;
    @Getter
    private CloudAPI.ServerState oldState;
    @Getter
    private CloudAPI.ServerState newState;

    public AsyncLobbyUpdateStateEvent(String serverId, String serverGroup, CloudAPI.ServerState oldState, CloudAPI.ServerState newState) {
        super(true);
        this.serverId = serverId;
        this.serverGroup = serverGroup;
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }


}
