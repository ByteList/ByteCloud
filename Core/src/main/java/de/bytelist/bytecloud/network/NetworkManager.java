package de.bytelist.bytecloud.network;

import lombok.Getter;

import java.util.logging.Logger;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class NetworkManager {

    private static int socketPort = 0;

    public static int getSocketPort() {
        if(socketPort < 4100 && socketPort > 6000) {
            System.out.println("[NetworkManager] Socket-Port must be higher than 4100 and lower than 6000! Using standard: 4213");
            return 4213;
        } else return socketPort;
    }

    @Getter
    private static Logger logger;

    public static void connect(int socketPort, Logger logger) {
            NetworkManager.socketPort = socketPort;
            NetworkManager.logger = logger;
    }
}
