package de.bytelist.bytecloud.network.bungee.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInStartServer extends Packet {

    public PacketInStartServer(String group, String sender) {
        super("PacketInStartServer");
        addProperty("group", group);
        addProperty("sender", sender);
    }
}
