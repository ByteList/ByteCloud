package de.bytelist.bytecloud.network.server;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInStopOwnServer extends Packet {

    public PacketInStopOwnServer(String serverId) {
        super(PacketName.IN_STOP_OWN_SERVER);
        addProperty("serverId", serverId);
    }
}
