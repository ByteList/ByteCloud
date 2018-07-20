package de.bytelist.bytecloud.core.event;

import de.bytelist.bytecloud.api.ServerState;
import lombok.Getter;

/**
 * Created by ByteList on 29.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudServerUpdateStateEvent extends CloudServerUpdateEvent {

    @Getter
    private ServerState oldState;
    @Getter
    private ServerState newState;

    public CloudServerUpdateStateEvent(String serverId, String serverGroup, ServerState oldState, ServerState newState) {
        super(serverId, serverGroup);
        this.oldState = oldState;
        this.newState = newState;
    }
}
