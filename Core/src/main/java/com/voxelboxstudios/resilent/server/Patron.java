package com.voxelboxstudios.resilent.server;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Patron {
    public static int CLIENT_ID = 0;
    private int id = CLIENT_ID;
    private Socket socket;
    private ResilentServer server;
    private BufferedWriter writer;
    private BufferedReader reader;
    private Thread thread;

    public Patron(ResilentServer paramResilentServer, Socket paramSocket) {
        CLIENT_ID += 1;
        this.socket = paramSocket;
        this.server = paramResilentServer;
        try {
            this.writer = new BufferedWriter(new OutputStreamWriter(paramSocket.getOutputStream()));
        } catch (IOException localIOException1) {
            localIOException1.printStackTrace();
        }
        try {
            this.reader = new BufferedReader(new InputStreamReader(paramSocket.getInputStream()));
        } catch (IOException localIOException2) {
            localIOException2.printStackTrace();
        }
        this.thread = new Thread(new ResilentPatronRunnable(this));
        this.thread.start();
    }

    public BufferedReader getReader() {
        return this.reader;
    }

    public BufferedWriter getWriter() {
        return this.writer;
    }

    public int getID() {
        return this.id;
    }

    public Socket getSocket() {
        return this.socket;
    }

    public ResilentServer getServer() {
        return this.server;
    }

    public void disconnect() {
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.socket = null;
            for (JsonServerListener localJsonServerListener : this.server.getListeners()) {
                localJsonServerListener.disconnected(this);
            }
        }
        try {
            this.writer.close();
            this.reader.close();
            this.writer = null;
            this.reader = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
    }

    public void sendPacket(JsonObject paramJsonObject) {
        try {
            this.writer.write(paramJsonObject.toString());
            this.writer.newLine();
            this.writer.flush();
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
    }

    public Thread getThread() {
        return this.thread;
    }

    public InetAddress getInetaddress() {
        if (this.socket == null) {
            return null;
        }
        return this.socket.getInetAddress();
    }
}
