package de.bytelist.bytecloud.network.bungee;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInBungeeStopped extends Packet {

    public PacketInBungeeStopped(String bungeeId) {
        super(PacketName.IN_BUNGEE_STPOPPED);
        addProperty("bungeeId", bungeeId);
    }
}
