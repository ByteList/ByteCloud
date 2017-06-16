package com.voxelboxstudios.resilent.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class ResilentServer {
    private ServerSocket socket;
    private Thread thread;
    private List listeners = new ArrayList();

    public List getListeners() {
        return this.listeners;
    }

    public void addListener(JsonServerListener paramJsonServerListener) {
        listeners.add(paramJsonServerListener);
    }

    public ServerSocket getSocket() {
        return this.socket;
    }

    public void start(int paramInt) throws IOException {
        this.socket = new ServerSocket(paramInt);
        this.thread = new Thread(new ResilentServerRunnable(this));
        this.thread.start();
    }

    public void start(String paramString, int paramInt1, int paramInt2) throws IOException {
        this.socket = new ServerSocket(paramInt1, paramInt2, InetAddress.getByName(paramString));
        this.thread = new Thread(new ResilentServerRunnable(this));
        this.thread.start();
    }

    public void start(String paramString, int paramInt) throws IOException {
        this.socket = new ServerSocket(paramInt, 50, InetAddress.getByName(paramString));
        this.thread = new Thread(new ResilentServerRunnable(this));
        this.thread.start();
    }

    public Thread getThread() {
        return this.thread;
    }

    public void close() throws IOException {
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
    }
}
