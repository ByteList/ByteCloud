package com.voxelboxstudios.resilent.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Iterator;

public class ResilentPatronRunnable
        implements Runnable {
    private Patron patron;

    public ResilentPatronRunnable(Patron paramPatron) {
        this.patron = paramPatron;
    }

    public void run() {
        while (this.patron.getReader() != null) {
            String str = null;
            try {
                str = this.patron.getReader().readLine();
            } catch (IOException localIOException1) {
                this.patron.disconnect();
            }
            if (str != null) {
                JsonParser localJsonParser = new JsonParser();
                JsonObject localJsonObject = null;
                try {
                    if ((localJsonParser.parse(str) instanceof JsonObject)) {
                        localJsonObject = (JsonObject) localJsonParser.parse(str);
                    }
                } catch (JsonSyntaxException localJsonSyntaxException) {
                    continue;
                }
                if (localJsonObject != null) {
                    Iterator localIterator = this.patron.getServer().getListeners().iterator();
                    while (localIterator.hasNext()) {
                        JsonServerListener localJsonServerListener = (JsonServerListener) localIterator.next();
                        localJsonServerListener.jsonReceived(this.patron, localJsonObject);
                    }
                }
            } else {
                this.patron.disconnect();
            }
        }
    }
}
