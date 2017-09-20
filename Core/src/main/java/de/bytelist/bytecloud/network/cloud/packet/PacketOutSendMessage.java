package de.bytelist.bytecloud.network.cloud.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutSendMessage extends Packet {

    public PacketOutSendMessage(String player, String message) {
        super("PacketOutSendMessage");
        addProperty("player", player);
        message = message.replace("§", "#&C#");
        addProperty("message", message);
    }
}