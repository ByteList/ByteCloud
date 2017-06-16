package com.voxelboxstudios.resilent.client;

import com.google.gson.JsonObject;
import de.bytelist.bytecloud.network.NetworkManager;

import java.io.IOException;

public class Client {
    private static ResilentClient client;

    public Client() throws IOException {
        client = new ResilentClient();

        client.connect("127.0.0.1", NetworkManager.getSocketPort());
    }

    public void addListener(JsonClientListener jsonClientListener) {
        client.addListener(jsonClientListener);
    }

    public static ResilentClient gC() {
        return client;
    }

    public static void sendPacket(JsonObject paramJsonObject) {
        try {
            client.sendPacket(paramJsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sP(JsonObject paramJsonObject) {
        try {
            client.sendPacket(paramJsonObject);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
