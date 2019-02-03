package de.bytelist.bytecloud.database;

import lombok.Getter;

/**
 * Created by ByteList on 22.04.2017.
 */
public enum DatabaseServerObject {

    GROUP("Group"),
    SERVER_ID("Server-Id"),
    PORT("Port"),
    STATE("State"),
    STARTED("Started"),
    SLOTS("Slots"),
    MOTD("Motd");

    @Getter
    private String name;

    DatabaseServerObject(String name) {
        this.name = name;
    }
}
