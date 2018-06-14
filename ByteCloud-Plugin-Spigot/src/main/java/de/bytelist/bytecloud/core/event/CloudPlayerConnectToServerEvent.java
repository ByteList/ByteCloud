package de.bytelist.bytecloud.core.event;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 14.06.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudPlayerConnectToServerEvent extends Event {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private String player;
    @Getter
    private String oldServer;
    @Getter
    private String targetServer;

    public CloudPlayerConnectToServerEvent(String player, String oldServer, String targetServer) {
        this.player = player;
        this.oldServer = oldServer;
        this.targetServer = targetServer;
    }


    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

}
