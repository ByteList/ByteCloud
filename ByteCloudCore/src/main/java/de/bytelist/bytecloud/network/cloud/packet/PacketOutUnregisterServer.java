package de.bytelist.bytecloud.network.cloud.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutUnregisterServer extends Packet {

    public PacketOutUnregisterServer(String serverId) {
        super("PacketOutUnregisterServer");
        addProperty("serverId", serverId);
    }
}
