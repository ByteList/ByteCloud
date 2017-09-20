package com.voxelboxstudios.resilent.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

public class ResilentClientRunnable implements Runnable {
    private ResilentClient client;

    public ResilentClientRunnable(ResilentClient paramResilentClient) {
        this.client = paramResilentClient;
    }

    public void run() {
        while (this.client.getReader() != null) {
            String str;
            try {
                str = this.client.getReader().readLine();
            } catch (IOException localIOException) {
                for (JsonClientListener jsonClientListener : this.client.getListeners()) {
                    jsonClientListener.disconnected();
                }
                break;
            }
            JsonElement jsonElement;
            if (str != null) {
                JsonParser jsonParser = new JsonParser();
                try {
                    jsonElement = jsonParser.parse(str);
                } catch (JsonSyntaxException localJsonSyntaxException) {
                    continue;
                }
                if (jsonElement != null) {
                    for (JsonClientListener jsonClientListener : this.client.getListeners()) {
                        jsonClientListener.jsonReceived(jsonElement.getAsJsonObject());
                    }
                }
            } else {
                for (JsonClientListener jsonClientListener : this.client.getListeners()) {
                    jsonClientListener.disconnected();
                }
                break;
            }
        }
    }
}
