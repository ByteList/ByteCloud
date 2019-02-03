package de.bytelist.bytecloud.common.spigot.event;

/**
 * Created by ByteList on 03.02.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface CloudServerUpdateEvent {

    public String getServerId();

    public String getServerGroup();
}