package de.bytelist.bytecloud.network.cloud;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutSendMessage extends Packet {

    public PacketOutSendMessage(String player, String message) {
        super(PacketName.OUT_SEND_MESSAGE);
        addProperty("player", player);
        message = message.replace("§", "#&C#");
        addProperty("message", message);
    }
}
