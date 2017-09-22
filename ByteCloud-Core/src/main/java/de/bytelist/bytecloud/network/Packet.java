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
    private final String name;

    private JsonObject jsonObject;


    public Packet(String name) {
        this.name = name;
        this.jsonObject = new JsonObject();
        this.jsonObject.addProperty("packet", name);
    }

    public void addProperty(String property, String value) {
        jsonObject.addProperty(property, value);
    }

    public JsonObject toJson() {
        return jsonObject;
    }

//    public enum PacketType {
//        OUT("Out"),
//        IN("In");
//
//        public String $;
//
//        PacketType(String $) {
//            this.$ = $;
//        }
//
////        public static PacketType getPacketTypeFromT(Class<?> clazz) {
////            for(PacketType packetType : PacketType.values()) {
////                if(packetType.$$ == clazz) {
////                    return packetType;
////                }
////            }
////            throw new IllegalArgumentException(clazz.getSimpleName() + "isn't a Packet Specifier!");
////        }
//    }
}
