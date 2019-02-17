package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.event.server.*;

import javax.crypto.SecretKey;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketCloudListener extends ServerAdapter {

    private SecretKey key;

    public ByteCloudPacketCloudListener(SecretKey key) {
        this.key = key;
    }

    @Override
    public void serverBound(ServerBoundEvent event) {
        System.out.println("PacketServer bound to " + event.getServer().getHost() + ":" + event.getServer().getPort());
    }

    @Override
    public void serverClosing(ServerClosingEvent event) {
        System.out.println("Closing PacketServer...");
    }

    @Override
    public void serverClosed(ServerClosedEvent event) {
        System.out.println("PacketServer closed.");
    }

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        ((ByteCloudPacketProtocol) event.getSession().getPacketProtocol()).setSecretKey(this.key);
    }
}
