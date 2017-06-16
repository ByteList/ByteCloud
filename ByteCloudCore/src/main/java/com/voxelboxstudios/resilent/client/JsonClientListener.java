package com.voxelboxstudios.resilent.client;

import com.google.gson.JsonObject;

public abstract class JsonClientListener {
    public abstract void jsonReceived(JsonObject paramJsonObject);

    public abstract void disconnected();

    public abstract void connected();
}
