package com.voxelboxstudios.resilent.server;

import com.google.gson.JsonObject;

public abstract class JsonServerListener {
    public abstract void jsonReceived(Patron patron, JsonObject jsonObject);

    public abstract void connected(Patron patron);

    public abstract void disconnected(Patron patron);
}
