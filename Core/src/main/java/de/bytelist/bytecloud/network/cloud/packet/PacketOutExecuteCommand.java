package de.bytelist.bytecloud.network.cloud.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 07.08.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutExecuteCommand extends Packet {

    public PacketOutExecuteCommand(String command) {
        super("PacketOutExecuteCommand");
        addProperty("command", command);
    }
}
