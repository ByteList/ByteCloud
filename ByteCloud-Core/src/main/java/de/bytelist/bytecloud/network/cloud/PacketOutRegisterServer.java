package de.bytelist.bytecloud.network.cloud;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutRegisterServer extends Packet {

    public PacketOutRegisterServer(String serverId, int port) {
        super(PacketName.OUT_REGISTER_PLAYER);
        addProperty("serverId", serverId);
        addProperty("port", String.valueOf(port));
    }
}
