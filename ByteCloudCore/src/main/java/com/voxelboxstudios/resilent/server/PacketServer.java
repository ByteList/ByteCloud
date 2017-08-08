package com.voxelboxstudios.resilent.server;

import java.io.IOException;

public class PacketServer {
    private ResilentServer resilentServer;

    public PacketServer(int port) throws IOException {
        resilentServer = new ResilentServer();

        resilentServer.start(port);
    }

    public void stop() {
        try {
            resilentServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addListener(JsonServerListener jsonServerListener) {
        resilentServer.addListener(jsonServerListener);
    }
}
