package de.bytelist.bytecloud.network.server;

import com.voxelboxstudios.resilent.client.Client;
import de.bytelist.bytecloud.network.Packet;
import lombok.Getter;

import java.io.IOException;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerClient {

    @Getter
    private Client client;

    public void z() {
        System.out.println("["+this.getClass().getSimpleName()+"] Starting...");
        try {
            this.client = new Client();
            this.client.addListener(new ServerClientListener());
            System.out.println("["+this.getClass().getSimpleName()+"] Started!");
        } catch (IOException e) {
            System.err.println("["+this.getClass().getSimpleName()+"] Error while starting: "+e);
        }
    }

    public void sendPacket(Packet packet) {
        if(packet.getName().startsWith("PacketIn")) {
            this.client.sP(packet.toJson());
        } else
            throw new IllegalArgumentException(packet.getName()+" can't sent to cloud.");
    }
}
