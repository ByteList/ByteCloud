package de.bytelist.bytecloud.common.bungee;

/**
 * Created by ByteList on 03.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface BungeeCloudPlugin {

    public BungeeCloudAPI getCloudAPI();

    public String getVersion();

    public  String getForcedJoinServerId();

}
