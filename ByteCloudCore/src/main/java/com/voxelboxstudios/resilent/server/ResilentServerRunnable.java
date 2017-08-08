package com.voxelboxstudios.resilent.server;

import java.io.IOException;
import java.net.Socket;

public class ResilentServerRunnable implements Runnable {

    private ResilentServer server;

    public ResilentServerRunnable(ResilentServer paramResilentServer) {
        this.server = paramResilentServer;
    }

    public void run() {
        while (this.server.getSocket() != null) {
            Socket localSocket;
            try {
                localSocket = this.server.getSocket().accept();
            } catch (IOException localIOException) {
                continue;
            }
            Patron localPatron = new Patron(this.server, localSocket);
            for (JsonServerListener jsonServerListener : this.server.getListeners()) {
                jsonServerListener.connected(localPatron);
            }
        }
    }
}
