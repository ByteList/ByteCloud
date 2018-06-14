package de.bytelist.bytecloud.network.bungee;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 14.06.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInPlayerChangedServer extends Packet {

    public PacketInPlayerChangedServer(String player, String oldServer, String targetServer) {
        super(PacketName.IN_PLAYER_CHANGED_SERVER);
        addProperty("player", player);
        addProperty("old", oldServer);
        addProperty("target", targetServer);
    }
}
