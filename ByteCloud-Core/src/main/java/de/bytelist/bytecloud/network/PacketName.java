package de.bytelist.bytecloud.network;

import de.bytelist.bytecloud.network.bungee.PacketInBungee;
import de.bytelist.bytecloud.network.bungee.PacketInBungeeStopped;
import de.bytelist.bytecloud.network.bungee.PacketInStartServer;
import de.bytelist.bytecloud.network.bungee.PacketInStopServer;
import de.bytelist.bytecloud.network.cloud.*;
import de.bytelist.bytecloud.network.server.PacketInChangeServerState;
import de.bytelist.bytecloud.network.server.PacketInKickPlayer;
import de.bytelist.bytecloud.network.server.PacketInServer;
import de.bytelist.bytecloud.network.server.PacketInStopOwnServer;
import lombok.Getter;

/**
 * Created by ByteList on 20.04.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum PacketName {

    NULL(null, null),

    /**
        package: de.bytelist.bytecloud.network.bungee
     */
    IN_BUNGEE(PacketType.BUNGEE, PacketInBungee.class.getSimpleName()),
    IN_BUNGEE_STPOPPED(PacketType.BUNGEE, PacketInBungeeStopped.class.getSimpleName()),
    IN_START_SERVER(PacketType.BUNGEE, PacketInStartServer.class.getSimpleName()),
    IN_STOP_SERVER(PacketType.BUNGEE, PacketInStopServer.class.getSimpleName()),

    /**
     package: de.bytelist.bytecloud.network.server
     */
    IN_CHANGE_SERVER_STATE(PacketType.SERVER, PacketInChangeServerState.class.getName()),
    IN_KICK_PLAYER(PacketType.SERVER, PacketInKickPlayer.class.getName()),
    IN_SERVER(PacketType.SERVER, PacketInServer.class.getName()),
    IN_STOP_OWN_SERVER(PacketType.SERVER, PacketInStopOwnServer.class.getName()),

    /**
     package: de.bytelist.bytecloud.network.cloud
     */
    OUT_CHANGE_SERVER_STATE(PacketType.CLOUD, PacketOutChangeServerState.class.getName()),
    OUT_CLOUD_INFO(PacketType.CLOUD, PacketOutCloudInfo.class.getName()),
    OUT_EXECUTE_COMMAND(PacketType.CLOUD, PacketOutExecuteCommand.class.getName()),
    @Deprecated OUT_KICK_ALL_PLAYERS(PacketType.CLOUD, PacketOutKickAllPlayers.class.getName()),
    OUT_KICK_PLAYER(PacketType.CLOUD, PacketOutKickPlayer.class.getName()),
    OUT_MOVE_PLAYER(PacketType.CLOUD, PacketOutMovePlayer.class.getName()),
    OUT_REGISTER_PLAYER(PacketType.CLOUD, PacketOutRegisterServer.class.getName()),
    OUT_SEND_MESSAGE(PacketType.CLOUD, PacketOutSendMessage.class.getName()),
    OUT_UNREGISTER_SERVER(PacketType.CLOUD, PacketOutUnregisterServer.class.getName());


    @Getter
    private PacketType packetType;
    @Getter
    private String packetName;

    PacketName(PacketType packetType, String name) {
        this.packetType = packetType;
        this.packetName = name;
    }

    public static PacketName getPacketName(String name) {
        for (PacketName packetName : values()) {
            if(name.equalsIgnoreCase(packetName.getPacketName())) {
                return packetName;
            }
        }
        return NULL;
    }
}
