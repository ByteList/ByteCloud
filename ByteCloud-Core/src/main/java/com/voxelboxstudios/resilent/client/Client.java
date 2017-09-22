package com.voxelboxstudios.resilent.client;

import com.google.gson.JsonObject;
import de.bytelist.bytecloud.network.NetworkManager;
import lombok.Getter;

import java.io.IOException;

public class Client {
    @Getter
    private ResilentClient client;

    public Client() throws IOException {
        client = new ResilentClient();

        client.connect("127.0.0.1", NetworkManager.getSocketPort());
    }

    public void addListener(JsonClientListener jsonClientListener) {
        client.addListener(jsonClientListener);
    }

    public void sendPacket(JsonObject paramJsonObject) {
        try {
            client.sendPacket(paramJsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
