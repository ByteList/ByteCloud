package de.bytelist.bytecloud.network.server;

import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

/**
 * Created by ByteList on 27.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketInChangeServerState extends Packet {

    public PacketInChangeServerState(String serverId, String serverState) {
        super(PacketName.IN_CHANGE_SERVER_STATE);
        addProperty("serverId", serverId);
        addProperty("serverState", serverState);
    }
}
