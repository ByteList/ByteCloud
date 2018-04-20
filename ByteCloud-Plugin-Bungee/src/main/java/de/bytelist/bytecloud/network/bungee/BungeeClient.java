package de.bytelist.bytecloud.network.bungee;

import com.voxelboxstudios.resilent.client.Client;
import de.bytelist.bytecloud.network.Packet;
import lombok.Getter;

import java.io.IOException;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BungeeClient {

    @Getter
    private Client client;

    public void connect() {
        System.out.println("["+this.getClass().getSimpleName()+"] Starting...");
        try {
            this.client = new Client();
            this.client.addListener(new BungeeClientListener());
            System.out.println("["+this.getClass().getSimpleName()+"] Started!");
        } catch (IOException e) {
            System.err.println("["+this.getClass().getSimpleName()+"] Error while starting: "+e);
        }
    }

    public void sendPacket(Packet packet) {
        if(packet.getName().getPacketName().startsWith("PacketIn")) {
            this.client.sendPacket(packet.toJson());
        } else
            throw new IllegalArgumentException(packet.getName()+" can't sent to cloud.");
    }

    public void disconnect() {
        System.out.println("["+this.getClass().getSimpleName()+"] Stopping...");
        try {
            this.client.getClient().disconnect();
            System.out.println("["+this.getClass().getSimpleName()+"] Stopped!");
        } catch (IOException e) {
            System.err.println("["+this.getClass().getSimpleName()+"] Error while stopping: "+e);
        }
    }
}
