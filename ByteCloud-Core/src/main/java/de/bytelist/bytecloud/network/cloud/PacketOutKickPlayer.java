package de.bytelist.bytecloud.network.cloud;

import com.google.common.base.Joiner;
import de.bytelist.bytecloud.network.Packet;
import de.bytelist.bytecloud.network.PacketName;

import java.util.ArrayList;

/**
 * Created by ByteList on 13.03.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PacketOutKickPlayer extends Packet {

    public PacketOutKickPlayer(String reason) {
        super(PacketName.OUT_KICK_PLAYER);
        addProperty("reason", reason.replace("§", "#&C#"));
        addProperty("players", "_all");
    }

    public PacketOutKickPlayer(String reason, String player) {
        super(PacketName.OUT_KICK_PLAYER);
        addProperty("reason", reason.replace("§", "#&C#"));
        addProperty("players", player);
    }

    public PacketOutKickPlayer(String reason, String... players) {
        super(PacketName.OUT_KICK_PLAYER);
        addProperty("reason", reason.replace("§", "#&C#"));
        addProperty("players", Joiner.on("#").join(players));
    }

    public PacketOutKickPlayer(String reason, ArrayList<String> players) {
        super(PacketName.OUT_KICK_PLAYER);
        addProperty("reason", reason.replace("§", "#&C#"));
        addProperty("players", Joiner.on("#").join(players));
    }
}
