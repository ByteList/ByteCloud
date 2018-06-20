package de.bytelist.bytecloud.core.event;

import lombok.Getter;

/**
 * Created by ByteList on 14.06.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum CloudEvent {

    UNKNOWN(-1),
    SERVER_UPDATE(0),
    SERVER_UPDATE_STATE(1),
    PLAYER_CONNECT(2);

    @Getter
    private int id;

    CloudEvent(int id) {
        this.id = id;
    }

    public static CloudEvent getCloudEventFromId(int id) {
        for (CloudEvent event : values()) {
            if(event.getId() == id) return event;
        }
        return UNKNOWN;
    }

    public static String createEventString(CloudEvent event, String... args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(event.getId()));
        for(String a : args) stringBuilder.append(":").append(a);

        return stringBuilder.toString();
    }
}
