package de.bytelist.bytecloud.network.server.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInStopOwnServer extends Packet {

    public PacketInStopOwnServer(String serverId) {
        super("PacketInStopOwnServer");
        addProperty("serverId", serverId);
    }
}
