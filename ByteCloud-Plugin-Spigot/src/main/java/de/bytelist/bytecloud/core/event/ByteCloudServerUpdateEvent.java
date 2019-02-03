package de.bytelist.bytecloud.core.event;

import de.bytelist.bytecloud.common.spigot.event.CloudServerUpdateEvent;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by ByteList on 29.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudServerUpdateEvent extends Event implements CloudServerUpdateEvent {

    @Getter
    private static HandlerList handlerList = new HandlerList();

    @Getter
    private String serverId;
    @Getter
    private String serverGroup;

    public ByteCloudServerUpdateEvent(String serverId, String serverGroup) {
        this.serverId = serverId;
        this.serverGroup = serverGroup;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }


}
