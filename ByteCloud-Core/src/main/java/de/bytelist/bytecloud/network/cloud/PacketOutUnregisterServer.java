package de.bytelist.bytecloud.network.cloud;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutUnregisterServer extends Packet {

    public PacketOutUnregisterServer(String serverId) {
        super(PacketName.OUT_UNREGISTER_SERVER);
        addProperty("serverId", serverId);
    }
}
