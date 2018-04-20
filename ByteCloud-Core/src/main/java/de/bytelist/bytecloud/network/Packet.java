package de.bytelist.bytecloud.network;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class Packet {
    @Getter
    private final PacketName name;

    private JsonObject jsonObject;


    public Packet(PacketName name) {
        this.name = name;
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("packet", name.getPacketName());
    }

    public void addProperty(String property, String value) {
        jsonObject.addProperty(property, value);
    }

    public JsonObject toJson() {
        return jsonObject;
    }

}
