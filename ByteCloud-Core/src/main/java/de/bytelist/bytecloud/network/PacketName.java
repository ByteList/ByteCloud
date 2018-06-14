package de.bytelist.bytecloud.network;

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
    IN_BUNGEE(PacketType.BUNGEE, "PacketInBungee"),
    IN_BUNGEE_STPOPPED(PacketType.BUNGEE, "PacketInBungeeStopped"),
    IN_START_SERVER(PacketType.BUNGEE, "PacketInStartServer"),
    IN_STOP_SERVER(PacketType.BUNGEE, "PacketInStopServer"),
    IN_PLAYER_CHANGED_SERVER(PacketType.BUNGEE, "PacketInPlayerChangedServer"),

    /**
     package: de.bytelist.bytecloud.network.server
     */
    IN_CHANGE_SERVER_STATE(PacketType.SERVER, "PacketInChangeServerState"),
    IN_KICK_PLAYER(PacketType.SERVER, "PacketInKickPlayer"),
    IN_SERVER(PacketType.SERVER, "PacketInServer"),
    IN_STOP_OWN_SERVER(PacketType.SERVER, "PacketInStopOwnServer"),

    /**
     package: de.bytelist.bytecloud.network.cloud
     */
    OUT_CLOUD_INFO(PacketType.CLOUD, "PacketOutCloudInfo"),
    OUT_EXECUTE_COMMAND(PacketType.CLOUD, "PacketOutExecuteCommand"),
    @Deprecated OUT_KICK_ALL_PLAYERS(PacketType.CLOUD, "PacketOutKickAllPlayers"),
    OUT_KICK_PLAYER(PacketType.CLOUD, "PacketOutKickPlayer"),
    OUT_MOVE_PLAYER(PacketType.CLOUD, "PacketOutMovePlayer"),
    OUT_REGISTER_PLAYER(PacketType.CLOUD, "PacketOutRegisterServer"),
    OUT_SEND_MESSAGE(PacketType.CLOUD, "PacketOutSendMessage"),
    OUT_UNREGISTER_SERVER(PacketType.CLOUD, "PacketOutUnregisterServer"),
    OUT_CALL_CLOUD_EVENT(PacketType.CLOUD, "PacketOutCallCloudEvent");


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
