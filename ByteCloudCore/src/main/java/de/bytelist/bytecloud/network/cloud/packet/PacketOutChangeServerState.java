package de.bytelist.bytecloud.network.cloud.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 29.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutChangeServerState  extends Packet {

    public PacketOutChangeServerState(String serverId, String serverGroup, String oldState, String newState) {
        super("PacketOutChangeServerState");
        addProperty("serverId", serverId);
        addProperty("serverGroup", serverGroup);
        addProperty("oldState", oldState);
        addProperty("newState", newState);
    }
}
