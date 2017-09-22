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
    PLAYER_MAX("Player-Max"),
    PLAYER_ONLINE("Player-Online"),
    SPECTATOR_MAX("Spectator-Max"),
    SPECTATOR_ONLINE("Spectator-Online"),
    MOTD("Motd"),
    EVENT_MODE("Event-Mode"),
    PLAYERS("Players"),
    SPECTATORS("Spectators");

    @Getter
    private String name;

    DatabaseServerObject(String name) {
        this.name = name;
    }
}
