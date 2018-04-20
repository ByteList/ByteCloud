package de.bytelist.bytecloud.network.bungee;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInStartServer extends Packet {

    public PacketInStartServer(String group, String sender) {
        super(PacketName.IN_START_SERVER);
        addProperty("group", group);
        addProperty("sender", sender);
    }
}
