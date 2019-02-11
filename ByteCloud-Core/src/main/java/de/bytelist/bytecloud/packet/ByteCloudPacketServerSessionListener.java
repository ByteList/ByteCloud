package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.event.session.*;
import de.bytelist.bytecloud.common.packet.PingPacket;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketServerSessionListener extends SessionAdapter {
    @Override
    public void packetReceived(PacketReceivedEvent event) {
        if(event.getPacket() instanceof PingPacket) {
            System.out.println("Received: " + event.<PingPacket>getPacket().getId());
            event.getSession().send(event.getPacket());
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
