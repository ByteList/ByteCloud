package de.bytelist.bytecloud.network.cloud.packet;

import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 12.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutCloudInfo extends Packet {

    public PacketOutCloudInfo(String cloudVersion, String cloudStarted, boolean cloudRunning) {
        super("PacketOutCloudInfo");
        addProperty("cloudVersion", cloudVersion);
        addProperty("cloudStarted", cloudStarted);
        addProperty("cloudRunning", String.valueOf(cloudRunning));
    }
}
