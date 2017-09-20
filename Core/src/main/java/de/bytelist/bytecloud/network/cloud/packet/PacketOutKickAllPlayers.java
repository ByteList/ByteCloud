package de.bytelist.bytecloud.network.cloud.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 01.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutKickAllPlayers extends Packet {

    public PacketOutKickAllPlayers(String reason) {
        super("PacketOutKickAllPlayers");
        reason = reason.replace("§", "#&C#");
        addProperty("reason", reason);
    }
}
