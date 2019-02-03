package de.bytelist.bytecloud.common.spigot;

import lombok.Getter;

/**
 * Created by ByteList on 03.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public final class SpigotCloud {

    @Getter
    private static SpigotCloudPlugin instance;

    public static void setInstance(SpigotCloudPlugin plugin) {
        if(instance != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Cloud instance");
        }
        instance = plugin;
    }
}
