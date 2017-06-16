package com.voxelboxstudios.resilent.client;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ResilentClient {
    private Socket socket;
    private List listeners = new ArrayList();
    private BufferedReader reader;
    private BufferedWriter writer;
    private Thread thread;

    public List getListeners() {
        return this.listeners;
    }

    public void addListener(JsonClientListener paramJsonClientListener) {
        this.listeners.add(paramJsonClientListener);
    }

    public Socket getSocket() {
        return this.socket;
    }

    public BufferedReader getReader() {
        return this.reader;
    }

    public BufferedWriter getWriter() {
        return this.writer;
    }

    public void connect(String paramString, int paramInt) throws IOException {
        this.socket = new Socket(paramString, paramInt);
        this.reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        this.thread = new Thread(new ResilentClientRunnable(this));
        this.thread.start();
        Iterator localIterator = getListeners().iterator();
        while (localIterator.hasNext()) {
            JsonClientListener localJsonClientListener = (JsonClientListener) localIterator.next();
            localJsonClientListener.connected();
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
        this.writer = null;
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
