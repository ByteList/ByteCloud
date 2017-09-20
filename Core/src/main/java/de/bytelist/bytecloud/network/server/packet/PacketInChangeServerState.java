package de.bytelist.bytecloud.network.server.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 27.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInChangeServerState extends Packet {

    public PacketInChangeServerState(String serverId, String serverState) {
        super("PacketInChangeServerState");
        addProperty("serverId", serverId);
        addProperty("serverState", serverState);
    }
}
