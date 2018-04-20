package de.bytelist.bytecloud.network.server;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInServer extends Packet {

    public PacketInServer(String serverId) {
        super(PacketName.IN_SERVER);
        addProperty("serverId", serverId);
    }
}
