package de.bytelist.bytecloud.network.server.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInServer extends Packet {

    public PacketInServer(String serverId) {
        super("PacketInServer");
        addProperty("serverId", serverId);
    }
}
