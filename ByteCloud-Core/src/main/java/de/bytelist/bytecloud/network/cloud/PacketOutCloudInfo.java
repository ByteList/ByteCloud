package de.bytelist.bytecloud.network.cloud;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 12.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutCloudInfo extends Packet {

    public PacketOutCloudInfo(String cloudVersion, String cloudStarted, boolean cloudRunning) {
        super(PacketName.OUT_CLOUD_INFO);
        addProperty("cloudVersion", cloudVersion);
        addProperty("cloudStarted", cloudStarted);
        addProperty("cloudRunning", String.valueOf(cloudRunning));
    }
}
