package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.event.session.*;
import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.packet.PingPacket;
import de.bytelist.bytecloud.common.packet.ServerStartedPacket;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketClientSessionListener extends SessionAdapter {
    @Override
    public void packetReceived(PacketReceivedEvent event) {
        switch (event.getPacket().getClass().getSimpleName()) {
            case PingPacket.PACKET_NAME:
                PingPacket packet = event.getPacket();

                System.out.println("CLIENT Received: " + packet.getId());

                if(packet.getId().equals("hello")) {
                    event.getSession().send(new PingPacket("exit"));
                } else if(packet.getId().equals("exit")) {
                    event.getSession().disconnect("Finished");
                }
                break;
            case ServerStartedPacket.PACKET_NAME:
                break;
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

        event.getSession().send(new ServerStartedPacket(Cloud.getInstance().getServerId()));
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
