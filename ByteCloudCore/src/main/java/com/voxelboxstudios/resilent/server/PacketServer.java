package com.voxelboxstudios.resilent.server;

import java.io.IOException;

public class PacketServer {
    private static ResilentServer resilentServer;

    public PacketServer(int paramInt) throws IOException {
        resilentServer = new ResilentServer();

        resilentServer.start(paramInt);
    }

    public void addListener(JsonServerListener jsonServerListener) {
        resilentServer.addListener(jsonServerListener);
    }
}
