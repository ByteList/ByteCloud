package de.bytelist.bytecloud.packet;

import com.github.steveice10.packetlib.event.server.*;

import javax.crypto.SecretKey;

/**
 * Created by ByteList on 11.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudPacketServerListener extends ServerAdapter {

    private SecretKey key;

    public ByteCloudPacketServerListener(SecretKey key) {
        this.key = key;
    }

    @Override
    public void serverBound(ServerBoundEvent event) {
        System.out.println("Bound: " + event.getServer().getHost() + ":" + event.getServer().getPort());
    }

    @Override
    public void serverClosing(ServerClosingEvent event) {
        System.out.println("CLOSING SERVER...");
    }

    @Override
    public void serverClosed(ServerClosedEvent event) {
        System.out.println("SERVER CLOSED");
    }

    @Override
    public void sessionAdded(SessionAddedEvent event) {
        System.out.println("Session Added: " + event.getSession().getHost() + ":" + event.getSession().getPort());
        ((ByteCloudPacketProtocol) event.getSession().getPacketProtocol()).setSecretKey(this.key);
    }

    @Override
    public void sessionRemoved(SessionRemovedEvent event) {
        System.out.println("Session Removed: " + event.getSession().getHost() + ":" + event.getSession().getPort());
    }
}
