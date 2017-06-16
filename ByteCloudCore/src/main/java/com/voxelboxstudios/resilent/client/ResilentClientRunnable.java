package com.voxelboxstudios.resilent.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Iterator;

public class ResilentClientRunnable
        implements Runnable {
    private ResilentClient client;

    public ResilentClientRunnable(ResilentClient paramResilentClient) {
        this.client = paramResilentClient;
    }

    public void run() {
        while (this.client.getReader() != null) {
            String str = null;
            Object localObject2;
            try {
                str = this.client.getReader().readLine();
            } catch (IOException localIOException) {
                Iterator localIterator1 = this.client.getListeners().iterator();
                while (localIterator1.hasNext()) {
                    localObject2 = (JsonClientListener) localIterator1.next();
                    ((JsonClientListener) localObject2).disconnected();
                }
                break;
            }
            Object localObject1;
            if (str != null) {
                localObject1 = new JsonParser();
                localObject2 = null;
                try {
                    localObject2 = (JsonObject) ((JsonParser) localObject1).parse(str);
                } catch (JsonSyntaxException localJsonSyntaxException) {
                    continue;
                }
                if (localObject2 != null) {
                    Iterator localIterator2 = this.client.getListeners().iterator();
                    while (localIterator2.hasNext()) {
                        JsonClientListener localJsonClientListener = (JsonClientListener) localIterator2.next();
                        localJsonClientListener.jsonReceived((JsonObject) localObject2);
                    }
                }
            } else {
                localObject2 = this.client.getListeners().iterator();
                while (((Iterator) localObject2).hasNext()) {
                    localObject1 = (JsonClientListener) ((Iterator) localObject2).next();
                    ((JsonClientListener) localObject1).disconnected();
                }
                break;
            }
        }
    }
}
