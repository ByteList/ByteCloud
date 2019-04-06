package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.event.session.*;
import de.bytelist.bytecloud.CloudAPIHandler;
import de.bytelist.bytecloud.common.packet.PacketInfo;
import de.bytelist.bytecloud.common.packet.client.ClientKeepAlivePacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudKeepAlivePacket;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketClientSessionListener extends SessionAdapter {
    @Override
    public void packetReceived(PacketReceivedEvent event) {
        PacketInfo packetInfo = PacketInfo.fromClass(event.getPacket().getClass());

        switch (packetInfo) {
            case UNKNOWN_PACKET:
                break;

            case CLIENT_KEEP_ALIVE_PACKET:
                break;
            case CLIENT_SERVER_CHANGE_STATE_PACKET:
                break;
            case CLIENT_SERVER_SET_MOTD_PACKET:
                break;
            case CLIENT_SERVER_START_PACKET:
                break;
            case CLIENT_SERVER_STARTED_PACKET:
                break;
            case CLIENT_SERVER_STOP_PACKET:
                break;
            case CLIENT_SERVER_STOPPED_PACKET:
                break;
            case CLIENT_PLAYER_CONNECT_PACKET:
                break;
            case CLIENT_PLAYER_DISCONNECT_PACKET:
                break;
            case CLIENT_PLAYER_KICK_PACKET:
                break;
            case CLIENT_PLAYER_SERVER_SWITCH_PACKET:
                break;


            case CLOUD_KEEP_ALIVE_PACKET:
                event.getSession().send(new ClientKeepAlivePacket(event.<CloudKeepAlivePacket>getPacket().getPingId()));
                break;
            case CLOUD_SERVER_CHANGED_STATE_PACKET:
                CloudAPIHandler.getInstance().setServerState(event.getPacket());
                break;
            case CLOUD_SERVER_STARTED_PACKET:
                CloudAPIHandler.getInstance().addCloudServer(event.getPacket());
                break;
            case CLOUD_SERVER_STOPPED_PACKET:
                CloudAPIHandler.getInstance().removeCloudServer(event.getPacket());
                break;
            case CLOUD_SERVER_GROUP_INFO_PACKET:
                CloudAPIHandler.getInstance().addCloudServerGroup(event.getPacket());
                break;
            case CLOUD_SERVER_SET_MOTD_PACKET:
                CloudAPIHandler.getInstance().setMotd(event.getPacket());
                break;
            case CLOUD_PLAYER_CONNECT_PACKET:
                CloudAPIHandler.getInstance().addCloudPlayer(event.getPacket());
                break;
            case CLOUD_PLAYER_DISCONNECT_PACKET:
                CloudAPIHandler.getInstance().removeCloudPlayer(event.getPacket());
                break;
            case CLOUD_PLAYER_KICK_PACKET:
                CloudAPIHandler.getInstance().kickCloudPlayer(event.getPacket());
                break;
            case CLOUD_PLAYER_MESSAGE_PACKET:
                CloudAPIHandler.getInstance().sendMessage(event.getPacket());
                break;
            case CLOUD_PLAYER_MOVE_TO_SERVER_PACKET:
                CloudAPIHandler.getInstance().moveCloudPlayer(event.getPacket());
                break;
            case CLOUD_PLAYER_SERVER_SWITCH_PACKET:
                CloudAPIHandler.getInstance().updateCloudPlayerCurrentServer(event.getPacket());
                break;
        }
    }

    @Override
    public void connected(ConnectedEvent event) {
        System.out.println("Connected to PacketServer.");
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
        System.out.println("Disconnecting from PacketServer: " + event.getReason());
        event.getCause().printStackTrace();
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        System.out.println("Disconnected from PacketServer: " + event.getReason());
    }
}
