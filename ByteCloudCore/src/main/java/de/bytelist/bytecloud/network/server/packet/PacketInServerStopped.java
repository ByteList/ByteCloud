package de.bytelist.bytecloud.network.server.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 01.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInServerStopped extends Packet {

    public PacketInServerStopped(String serverId) {
        super("PacketInServerStopped");
        addProperty("serverId", serverId);
    }
}
