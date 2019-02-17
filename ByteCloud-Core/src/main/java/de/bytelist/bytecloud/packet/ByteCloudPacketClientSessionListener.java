package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.event.session.*;
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
            case CLIENT_SERVER_STARTED_PACKET:
                break;
            case CLIENT_SERVER_STOPPED_PACKET:
                break;

            case CLOUD_KEEP_ALIVE_PACKET:
                event.getSession().send(new ClientKeepAlivePacket(event.<CloudKeepAlivePacket>getPacket().getPingId()));
                break;
            case CLOUD_SERVER_STARTED_PACKET:
                break;
            case CLOUD_SERVER_STOPPED_PACKET:
                break;
            case CLOUD_SERVER_INFO_PACKET:
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
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        System.out.println("Disconnected from PacketServer: " + event.getReason());
    }
}
