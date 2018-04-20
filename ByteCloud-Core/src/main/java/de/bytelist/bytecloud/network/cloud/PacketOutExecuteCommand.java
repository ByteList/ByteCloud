package de.bytelist.bytecloud.network.cloud;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 07.08.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutExecuteCommand extends Packet {

    public PacketOutExecuteCommand(String command) {
        super(PacketName.OUT_EXECUTE_COMMAND);
        addProperty("command", command);
    }
}
