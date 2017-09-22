package de.bytelist.bytecloud.file;


import lombok.Getter;

/**
 * Created by ByteList on 31.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public enum EnumFile {

    CLOUD("./Cloud/"),
    CLOUD_LOGS(CLOUD.path+"logs/"),
    BUNGEE("./Bungee/"),
    GENERALS("./Generals/"),
    GENERALS_PLUGINS(GENERALS.path+"plugins/"),
    GENERALS_SPIGOT(GENERALS.path+"spigot/"),
    SERVERS("./Servers/"),
    SERVERS_RUNNING(SERVERS.path+"running/"),
    SERVERS_PERMANENT(SERVERS.path+"permanent/"),
    SERVERS_LOGS(SERVERS.path+"logs/"),
    TEMPLATES("./Templates/");


    @Getter
    private String path;

    EnumFile(String path) {
        this.path = path;
    }
}
