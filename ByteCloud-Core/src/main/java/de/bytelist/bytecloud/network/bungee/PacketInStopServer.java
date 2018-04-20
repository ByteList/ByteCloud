package de.bytelist.bytecloud.network.bungee;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInStopServer extends Packet {

    public PacketInStopServer(String serverId, String sender) {
        super(PacketName.IN_STOP_SERVER);
        addProperty("serverId", serverId);
        addProperty("sender", sender);
    }
}
