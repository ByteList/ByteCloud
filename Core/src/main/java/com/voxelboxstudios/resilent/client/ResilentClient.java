package com.voxelboxstudios.resilent.client;

import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ResilentClient {
    @Getter
    private Socket socket;
    @Getter
    private ArrayList<JsonClientListener> listeners = new ArrayList<>();
    @Getter
    private BufferedReader reader;
    @Getter
    private BufferedWriter writer;
    private Thread thread;

    public void addListener(JsonClientListener jsonClientListener) {
        this.listeners.add(jsonClientListener);
    }


    public void connect(String paramString, int paramInt) throws IOException {
        this.socket = new Socket(paramString, paramInt);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.thread = new Thread(new ResilentClientRunnable(this));
        this.thread.start();
        for (JsonClientListener jsonClientListener : listeners) {
            jsonClientListener.connected();
        }
    }

    public void disconnect() throws IOException {
        if (this.socket != null) {
            this.socket.close();
            this.socket = null;
        }
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        this.writer.close();
        this.writer = null;
        this.reader.close();
        this.reader = null;
    }

    public void sendPacket(JsonObject paramJsonObject) throws IOException {
        this.writer.write(paramJsonObject.toString());
        this.writer.newLine();
        this.writer.flush();
    }

    public Thread getThread() {
        return this.thread;
    }
}
