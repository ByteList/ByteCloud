package de.bytelist.bytecloud.network;

import de.bytelist.bytecloud.network.bungee.BungeeClient;
import de.bytelist.bytecloud.network.cloud.CloudServer;
import de.bytelist.bytecloud.network.server.ServerClient;
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
    private static ConnectType connectType;

    @Getter
    private static CloudServer cloudServer;
    @Getter
    private static BungeeClient bungeeClient;
    @Getter
    private static ServerClient serverClient;
    @Getter
    private static Logger logger;

    public static void connect(ConnectType connectType, int socketPort, Logger logger) {
            NetworkManager.socketPort = socketPort;
            NetworkManager.connectType = connectType;
            NetworkManager.logger = logger;
            switch (connectType) {
                case CLOUD:
                    cloudServer = new CloudServer();
                    cloudServer.z();
                    break;
                case BUNGEE:
                    bungeeClient = new BungeeClient();
                    bungeeClient.z();
                    break;
                case SERVER:
                    serverClient = new ServerClient();
                    serverClient.z();
                    break;
            }
    }

    public static void sendPacket(Packet packet) {
        switch (connectType) {
            case CLOUD:
                throw new UnsupportedOperationException("Not supported. Use CloudServer#sendPacket()");
            case BUNGEE:
                bungeeClient.sendPacket(packet);
                break;
            case SERVER:
                bungeeClient.sendPacket(packet);
                break;
        }
    }

    public enum ConnectType {
        CLOUD, BUNGEE, SERVER
    }
}
