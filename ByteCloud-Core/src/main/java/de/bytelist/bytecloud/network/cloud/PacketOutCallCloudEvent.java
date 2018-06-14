package de.bytelist.bytecloud.network.cloud;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 14.06.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutCallCloudEvent extends Packet {

    public PacketOutCallCloudEvent(String event) {
        super(PacketName.OUT_CALL_CLOUD_EVENT);
        addProperty("event", event);
    }
}
