package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.event.session.*;
import de.bytelist.bytecloud.common.packet.PingPacket;
import de.bytelist.bytecloud.common.packet.ServerInfoPacket;
import de.bytelist.bytecloud.common.packet.ServerStartedPacket;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketServerSessionListener extends SessionAdapter {
    @Override
    public void packetReceived(PacketReceivedEvent event) {
        switch (event.getPacket().getClass().getSimpleName()) {
            case PingPacket.PACKET_NAME:
                PingPacket packet = event.getPacket();

                System.out.println("Received: " + packet.getId());
                break;
            case ServerStartedPacket.PACKET_NAME:
//                Cloud.getInstance().
// TODO: 11.02.2019 add common-software
                break;
            case ServerInfoPacket.PACKET_NAME:

                break;
        }
    }

    @Override
    public void packetSent(PacketSentEvent event) {
        if(event.getPacket() instanceof PingPacket) {
            System.out.println("Sent: " + event.<PingPacket>getPacket().getId());
        }
    }

    @Override
    public void connected(ConnectedEvent event) {
        System.out.println("Connected");
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
        System.out.println("Disconnecting: " + event.getReason());
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        System.out.println("Disconnected: " + event.getReason());
    }
}
