package de.bytelist.bytecloud.network.bungee;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInBungee extends Packet {


    public PacketInBungee(String bungeeId, Integer port) {
        super(PacketName.IN_BUNGEE);

        addProperty("bungeeId", bungeeId);
        addProperty("port", port.toString());
    }
}
