package de.bytelist.bytecloud.common.bungee;

import lombok.Getter;

/**
 * Created by ByteList on 03.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public final class BungeeCloud {

    @Getter
    private static BungeeCloudPlugin instance;

    public static void setInstance(BungeeCloudPlugin plugin) {
        if(instance != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Cloud instance");
        }
        instance = plugin;
    }

}
