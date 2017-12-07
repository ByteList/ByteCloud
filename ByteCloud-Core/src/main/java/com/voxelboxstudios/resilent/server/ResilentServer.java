package com.voxelboxstudios.resilent.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ResilentServer {
    private ServerSocket socket;
    private Thread thread;
    private ArrayList<JsonServerListener> listeners = new ArrayList<>();

    public ArrayList<JsonServerListener> getListeners() {
        return this.listeners;
    }

    public void addListener(JsonServerListener paramJsonServerListener) {
        listeners.add(paramJsonServerListener);
    }

    public ServerSocket getSocket() {
        return this.socket;
    }

    public void start(int port) throws IOException {
        this.socket = new ServerSocket(port);
        this.thread = new Thread(new ResilentServerRunnable(this), "Packet-Thread");
        this.thread.start();
    }
//
//    public void start(String address, int port, int backlog) throws IOException {
//        this.socket = new ServerSocket(port, backlog, InetAddress.getByName(address));
//        this.thread = new Thread(new ResilentServerRunnable(this), "Packet-Thread");
//        this.thread.start();
//    }
//
//    public void start(String address, int port) throws IOException {
//        this.socket = new ServerSocket(port, 50, InetAddress.getByName(address));
//        this.thread = new Thread(new ResilentServerRunnable(this), "Packet-Thread");
//        this.thread.start();
//    }

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
