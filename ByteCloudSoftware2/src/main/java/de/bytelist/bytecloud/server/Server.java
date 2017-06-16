package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.network.cloud.packet.*;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import static de.bytelist.bytecloud.network.NetworkManager.getCloudServer;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class Server extends Thread {

    /*@Getter
    private final ServerType serverType;*/
    @Getter
    private final String serverId;
    @Getter
    private final int port;
    @Getter
    private int ramM, maxPlayer, maxSpectator;
    @Getter
    private final ServerState serverState;
    @Getter
    private boolean running;
    @Getter
    private final File directory;
    @Getter
    private Process process;

    public Server(/*ServerType serverType, */String serverId, int port, ServerState serverState, String directory) {
//        this.serverType = serverType;
        this.serverId = serverId;
        this.port = port;
        this.serverState = serverState;
        this.running = false;
        this.directory = new File(directory, serverId);
    }

    @Override
    public void run() {
        this.running = true;
        if (process == null) {
            String[] param =
                    {"java", "-Xmx" + ramM + "M", "-Dde.bytelist.bytecloud.servername="+serverId, "-Dfile.encoding=UTF-8", "-Dcom.mojang.eula.agree=true",
                            "-jar", "spigot-"+ ByteCloud.getInstance().getCloudProperties().getProperty("spigot-version")+".jar", "-s",
                            String.valueOf((maxPlayer + maxSpectator)), "-o", "false", "-p", String.valueOf(port)};
            ProcessBuilder pb = new ProcessBuilder(param);
            pb.directory(directory);
            try {
                process = pb.start();
                ByteCloud.getInstance().getServerHandler().setAreServersRunning();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startServer(int ramM, int maxPlayer, int maxSpectator) {
        if (!running) {
            ByteCloud.getInstance().getLogger().info("Server " + serverId + " is starting on port " + port + ".");
            this.ramM = ramM;
            this.maxPlayer = maxPlayer;
            this.maxSpectator = maxSpectator;
            start();
        }
    }

    public void stopServer(StopType stopType) {
        if (running) {
            ByteCloud.getInstance().getLogger().info("Server " + serverId + " is stopping.");
            switch (stopType) {
                case KICK:
                    PacketOutKickAllPlayers packetOutKickAllPlayers = new PacketOutKickAllPlayers("§6Du wurdest vom Netzwerk gekickt.");
                    getCloudServer().sendPacket(serverId, packetOutKickAllPlayers);
                    break;
                case MOVE_TO_LOBBY:
                    ArrayList<String> players = new ArrayList<>();
                    players.addAll(Arrays.asList(ByteCloud.getInstance().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PLAYERS).getAsString().split(",")));
                    players.addAll(Arrays.asList(ByteCloud.getInstance().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.SPECTATORS).getAsString().split(",")));

                    PacketOutMovePlayer packetOutMovePlayer = new PacketOutMovePlayer(
                            ByteCloud.getInstance().getServerHandler().getRandomLobbyId(),
                            "§6Du wurdest auf die Lobby verschoben.",
                            players);
                    getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutMovePlayer);
                    break;
            }
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), new PacketOutUnregisterServer(serverId));
            getCloudServer().sendPacket(serverId, new PacketOutStopServer());
        }
    }

    public void onStart() {
        if(running) {
            getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), new PacketOutRegisterServer(serverId, port));
            getCloudServer().sendPacket(serverId, new PacketOutCloudInfo());
            ByteCloud.getInstance().getLogger().info("Server " + serverId + " started. RAM: "+this.ramM+" Slots: "+(this.maxPlayer+this.maxSpectator));
        }
    }

    public void onStop() {
        if(ByteCloud.getInstance().getDatabaseServer().existsServer(this.serverId)) {
            ByteCloud.getInstance().getDatabaseServer().removeServer(this.serverId);
        }
        if(running) {
            this.running = false;
            if(process != null) {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.process.destroy();
                this.process = null;
            }
            ByteCloud.getInstance().getLogger().info("Server " + serverId + " stopped.");
//            ByteCloud.getInstance().getServerHandler().servers--;
        }
    }

    public enum ServerState {
        STARTING,
        LOBBY,
        FULL,
        INGAME,
        RESTART,
        STOPPED
    }

    public enum StopType {
        KICK,
        MOVE_TO_LOBBY
    }
}
