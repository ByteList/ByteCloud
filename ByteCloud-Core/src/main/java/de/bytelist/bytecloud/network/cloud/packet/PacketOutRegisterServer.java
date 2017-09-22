package de.bytelist.bytecloud.network.cloud.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutRegisterServer extends Packet {

    public PacketOutRegisterServer(String serverId, int port) {
        super("PacketOutRegisterServer");
        addProperty("serverId", serverId);
        addProperty("port", String.valueOf(port));
    }
}
