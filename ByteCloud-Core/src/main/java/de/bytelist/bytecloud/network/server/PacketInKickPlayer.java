package de.bytelist.bytecloud.network.server;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInKickPlayer extends Packet {

    public PacketInKickPlayer(String player, String reason) {
        super(PacketName.IN_KICK_PLAYER);
        addProperty("player", player);
        addProperty("reason", reason);
    }
}
