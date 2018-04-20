package de.bytelist.bytecloud.network.cloud;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 29.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutChangeServerState extends Packet {

    public PacketOutChangeServerState(String serverId, String serverGroup, String oldState, String newState) {
        super(PacketName.OUT_CHANGE_SERVER_STATE);
        addProperty("serverId", serverId);
        addProperty("serverGroup", serverGroup);
        addProperty("oldState", oldState);
        addProperty("newState", newState);
    }
}
