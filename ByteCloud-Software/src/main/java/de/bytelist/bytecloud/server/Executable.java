package de.bytelist.bytecloud.server;

/**
 * Created by ByteList on 09.08.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface Executable {

    boolean startServer(String sender);

    boolean stopServer(String sender);

    void onStart();
}
