package de.bytelist.bytecloud.core.event;

import de.bytelist.bytecloud.core.cloud.CloudAPI;
import lombok.Getter;

/**
 * Created by ByteList on 29.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudServerUpdateStateEvent extends CloudServerUpdateEvent {

    @Getter
    private CloudAPI.ServerState oldState;
    @Getter
    private CloudAPI.ServerState newState;

    public CloudServerUpdateStateEvent(String serverId, String serverGroup, CloudAPI.ServerState oldState, CloudAPI.ServerState newState) {
        super(serverId, serverGroup);
        this.oldState = oldState;
        this.newState = newState;
    }
}
