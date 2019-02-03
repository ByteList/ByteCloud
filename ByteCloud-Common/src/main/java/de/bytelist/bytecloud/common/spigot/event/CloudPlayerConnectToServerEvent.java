package de.bytelist.bytecloud.common.spigot.event;

/**
 * Created by ByteList on 03.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface CloudPlayerConnectToServerEvent {

    public String getPlayer();

    public String getOldServer();

    public String getOldServerGroup();

    public String getTargetServer();

    public String getTargetServerGroup();

}
