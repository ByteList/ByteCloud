package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.event.session.*;
import de.bytelist.bytecloud.common.packet.PingPacket;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketClientSessionListener extends SessionAdapter {
    @Override
    public void packetReceived(PacketReceivedEvent event) {
        if(event.getPacket() instanceof PingPacket) {
            PingPacket packet = event.getPacket();

            System.out.println("CLIENT Received: " + packet.getId());

            if(packet.getId().equals("hello")) {
                event.getSession().send(new PingPacket("exit"));
            } else if(packet.getId().equals("exit")) {
                event.getSession().disconnect("Finished");
            }
        }
    }

    @Override
    public void packetSent(PacketSentEvent event) {
        if(event.getPacket() instanceof PingPacket) {
            System.out.println("CLIENT Sent: " + event.<PingPacket>getPacket().getId());
        }
    }

    @Override
    public void connected(ConnectedEvent event) {
        System.out.println("CLIENT Connected");

        event.getSession().send(new PingPacket("hello"));
    }

    @Override
    public void disconnecting(DisconnectingEvent event) {
        System.out.println("CLIENT Disconnecting: " + event.getReason());
    }

    @Override
    public void disconnected(DisconnectedEvent event) {
        System.out.println("CLIENT Disconnected: " + event.getReason());
    }
}
