package de.bytelist.bytecloud.network.bungee.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInBungee extends Packet {


    public PacketInBungee(String bungeeId, Integer port) {
        super("PacketInBungee");

        addProperty("bungeeId", bungeeId);
        addProperty("port", port.toString());
    }
}
