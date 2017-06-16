package de.bytelist.bytecloud.network.cloud.packet;

import com.google.common.base.Joiner;
import de.bytelist.bytecloud.network.Packet;

import java.util.ArrayList;

/**
 * Created by ByteList on 01.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutMovePlayer extends Packet {

    public PacketOutMovePlayer(String serverId, String reason) {
        super("PacketOutMovePlayer");
        addProperty("serverId", serverId);
        addProperty("reason", reason);
        addProperty("players", "_all");
    }

    public PacketOutMovePlayer(String serverId, String reason, String player) {
        super("PacketOutMovePlayer");
        addProperty("serverId", serverId);
        addProperty("reason", reason);
        addProperty("players", player);
    }

    public PacketOutMovePlayer(String serverId, String reason, String... players) {
        super("PacketOutMovePlayer");
        addProperty("serverId", serverId);
        addProperty("reason", reason);
        addProperty("players", Joiner.on("#").join(players));
    }

    public PacketOutMovePlayer(String serverId, String reason, ArrayList<String> players) {
        super("PacketOutMovePlayer");
        addProperty("serverId", serverId);
        addProperty("reason", reason);
        addProperty("players", Joiner.on("#").join(players));
    }
}
