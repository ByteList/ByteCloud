package com.voxelboxstudios.resilent.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

public class ResilentServerRunnable
        implements Runnable {
    private ResilentServer server;

    public ResilentServerRunnable(ResilentServer paramResilentServer) {
        this.server = paramResilentServer;
    }

    public void run() {
        while (this.server.getSocket() != null) {
            Socket localSocket = null;
            try {
                localSocket = this.server.getSocket().accept();
            } catch (IOException localIOException) {
                continue;
            }
            Patron localPatron = new Patron(this.server, localSocket);
            Iterator localIterator = this.server.getListeners().iterator();
            while (localIterator.hasNext()) {
                JsonServerListener localJsonServerListener = (JsonServerListener) localIterator.next();
                localJsonServerListener.connected(localPatron);
            }
        }
    }
}
