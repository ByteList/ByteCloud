package de.bytelist.bytecloud.network.cloud;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 01.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
@Deprecated
public class PacketOutKickAllPlayers extends Packet {

    @Deprecated
    public PacketOutKickAllPlayers(String reason) {
        super(PacketName.OUT_KICK_ALL_PLAYERS);
        reason = reason.replace("§", "#&C#");
        addProperty("reason", reason);
    }
}
