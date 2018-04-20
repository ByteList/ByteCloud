package de.bytelist.bytecloud.network.cloud;

import com.google.common.base.Joiner;
import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

import java.util.ArrayList;

/**
 * Created by ByteList on 01.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutMovePlayer extends Packet {

    public PacketOutMovePlayer(String serverId, String reason) {
        super(PacketName.OUT_MOVE_PLAYER);
        addProperty("serverId", serverId);
        addProperty("reason", reason.replace("§", "#&C#"));
        addProperty("players", "_all");
    }

    public PacketOutMovePlayer(String serverId, String reason, String player) {
        super(PacketName.OUT_MOVE_PLAYER);
        addProperty("serverId", serverId);
        addProperty("reason", reason.replace("§", "#&C#"));
        addProperty("players", player);
    }

    public PacketOutMovePlayer(String serverId, String reason, String... players) {
        super(PacketName.OUT_MOVE_PLAYER);
        addProperty("serverId", serverId);
        addProperty("reason", reason.replace("§", "#&C#"));
        addProperty("players", Joiner.on("#").join(players));
    }

    public PacketOutMovePlayer(String serverId, String reason, ArrayList<String> players) {
        super(PacketName.OUT_MOVE_PLAYER);
        addProperty("serverId", serverId);
        addProperty("reason", reason.replace("§", "#&C#"));
        addProperty("players", Joiner.on("#").join(players));
    }
}
