package de.bytelist.bytecloud.common.spigot.event;

import de.bytelist.bytecloud.common.ServerState;

/**
 * Created by ByteList on 03.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface CloudServerUpdateStateEvent extends CloudServerUpdateEvent {

    public ServerState getOldState();

    public ServerState getNewState();
}
