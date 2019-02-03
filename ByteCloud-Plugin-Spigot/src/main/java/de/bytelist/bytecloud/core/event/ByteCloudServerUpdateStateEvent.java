package de.bytelist.bytecloud.core.event;

import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.spigot.event.CloudServerUpdateStateEvent;
import lombok.Getter;

/**
 * Created by ByteList on 29.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudServerUpdateStateEvent extends ByteCloudServerUpdateEvent implements CloudServerUpdateStateEvent {

    @Getter
    private ServerState oldState;
    @Getter
    private ServerState newState;

    public ByteCloudServerUpdateStateEvent(String serverId, String serverGroup, ServerState oldState, ServerState newState) {
        super(serverId, serverGroup);
        this.oldState = oldState;
        this.newState = newState;
    }
}
