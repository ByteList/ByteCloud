package de.bytelist.bytecloud.network.bungee.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInStopServer extends Packet {

    public PacketInStopServer(String serverId, String sender) {
        super("PacketInStopServer");
        addProperty("serverId", serverId);
        addProperty("sender", sender);
    }
}
