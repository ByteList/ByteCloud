package de.bytelist.bytecloud.network.cloud;

import com.voxelboxstudios.resilent.server.PacketServer;
import com.voxelboxstudios.resilent.server.Patron;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.Packet;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudServer {

    private HashMap<Integer, String> ids = new HashMap<>();
    private HashMap<String, Integer> clients = new HashMap<>();
    private HashMap<Integer, Patron> patrons = new HashMap<>();

    @Getter
    private PacketServer packetServer;

    public boolean startPacketServer() {
        try {
            this.packetServer = new PacketServer(NetworkManager.getSocketPort());
            this.packetServer.addListener(new CloudServerListener());
            NetworkManager.getLogger().info("Packet-Server started!");
            return true;
        } catch (IOException e) {
            NetworkManager.getLogger().warning("Error while starting Packet-Server: "+e);
            return false;
        }
    }

    public Patron getPatron(String client) {
        if(this.clients.containsKey(client)) {
            return this.patrons.get(this.clients.get(client));
        }
        return null;
    }

    public String getServerId(int clientId) {
        if(this.ids.containsKey(clientId)) {
            return this.ids.get(clientId);
        }
        return null;
    }

    void registerClient(String client, Patron patron) {
        int clientId = patron.getID();
        if((!this.clients.containsKey(client)) && (!this.clients.containsValue(clientId))) {
            this.clients.put(client, clientId);
            this.ids.put(clientId, client);
            if(!this.patrons.containsKey(clientId)) {
                this.patrons.put(clientId, patron);
                NetworkManager.getLogger().info("Client "+patron.getID()+"("+client+") registered!");
                return;
            }
        }
        NetworkManager.getLogger().warning("Client "+patron.getID()+"("+client+") can't registered!");
    }

    void unregisterClient(Patron patron) {
        int clientId = patron.getID();
        String client = this.ids.get(clientId);
        if(this.patrons.containsKey(clientId))
            this.patrons.remove(clientId);
        if(this.clients.containsKey(client))
            this.clients.remove(client);
        if(this.ids.containsKey(clientId))
            this.ids.remove(clientId);
        NetworkManager.getLogger().info("Client "+patron.getID()+"("+client+") unregistered!");
    }

    public void sendPacket(Patron patron, Packet packet) {
        if(packet.getName().startsWith("PacketOut")) {
            patron.sendPacket(packet.toJson());
        } else
            throw new IllegalArgumentException(packet.getName()+" can't sent to server.");
    }

    public void sendPacket(String serverId, Packet packet) {
        if(packet.getName().startsWith("PacketOut")) {
            if(clients.containsKey(serverId)) {
                patrons.get(clients.get(serverId)).sendPacket(packet.toJson());
            }
            else throw new NullPointerException(serverId+" hasn't a client id.");
        }
        else throw new IllegalArgumentException(packet.getName()+" can't sent to server.");
    }
}
