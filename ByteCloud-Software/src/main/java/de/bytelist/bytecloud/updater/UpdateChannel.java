package de.bytelist.bytecloud.updater;

import lombok.Getter;

/**
 * Created by ByteList on 16.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum UpdateChannel {

    STABLE("stable"),
    DEV("dev");

    @Getter
    private final String channel;

    UpdateChannel(String channel) {
        this.channel = channel;
    }

    public static UpdateChannel getUpdateChannel(String channel) {
        for (UpdateChannel c : values()) if (c.getChannel().equalsIgnoreCase(channel)) return c;
        throw new IllegalArgumentException("Channel not found!");
    }
}
