package de.bytelist.bytecloud.network.cloud.packet;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.network.Packet;

/**
 * Created by ByteList on 12.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutCloudInfo extends Packet {

    public PacketOutCloudInfo() {
        super("PacketOutCloudInfo");
        ByteCloud byteCloud = ByteCloud.getInstance();
        addProperty("cloudVersion", byteCloud.getVersion());
        addProperty("cloudStarted", byteCloud.getCloudStarted());
        addProperty("cloudRunning", String.valueOf(byteCloud.isRunning));
    }
}
