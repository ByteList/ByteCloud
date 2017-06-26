package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.cloud.packet.*;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

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
public abstract class Server {

    @Getter
    private final String serverId;
    @Getter
    private final int port;
    @Getter
    private int ramM, maxPlayer, maxSpectator;
    @Getter
    private final ServerState serverState;
    @Getter
    private final File directory;
    @Getter
    private Process process;

    String SERVER_KICK_MESSAGE;

    public Server(String serverId, int port, ServerState serverState, String directory) {
        this.serverId = serverId;
        this.port = port;
        this.serverState = serverState;
        this.directory = new File(directory, serverId);

        this.SERVER_KICK_MESSAGE = "§6Du wurdest vom Netzwerk gekickt.";
    }

    public void startServer(int ramM, int maxPlayer, int maxSpectator) {
        ByteCloud.getInstance().getLogger().info("Server " + serverId + " is starting on port " + port + ".");
        this.ramM = ramM;
        this.maxPlayer = maxPlayer;
        this.maxSpectator = maxSpectator;
        if (process == null) {
            String[] param =
                    { "java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-Xmn2M", "-Xmx" + ramM + "M", "-Dde.bytelist.bytecloud.servername="+serverId, "-Dfile.encoding=UTF-8", "-Dcom.mojang.eula.agree=true",
                            "-jar", "spigot-"+ ByteCloud.getInstance().getCloudProperties().getProperty("spigot-version")+".jar", "-s",
                            String.valueOf((maxPlayer + maxSpectator)), "-o", "false", "-p", String.valueOf(port), "nogui"};
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

    public void stopServer(StopType stopType) {
        if(this.process != null) {
            if(this.process.isAlive()) {
                ByteCloud.getInstance().getLogger().info("Server " + serverId + " is stopping.");
                switch (stopType) {
                    case KICK:
                        PacketOutKickAllPlayers packetOutKickAllPlayers = new PacketOutKickAllPlayers(SERVER_KICK_MESSAGE);
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
                    this.process.getOutputStream().write("stop\n".getBytes());
                    this.process.getOutputStream().flush();
                    Thread.sleep(1500L);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
//      ByteCloud.getInstance().getLogger().info("Server " + serverId + " is stopping.");

        getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), new PacketOutUnregisterServer(serverId));

        ByteCloud.getInstance().getLogger().info("Server " + serverId + " stopped.");
    }

    public void onStart() {
        if(this.process != null) {
            getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), new PacketOutRegisterServer(serverId, port));
            getCloudServer().sendPacket(serverId, new PacketOutCloudInfo());
            ByteCloud.getInstance().getLogger().info("Server " + serverId + " started. RAM: "+this.ramM+" Slots: "+(this.maxPlayer+this.maxSpectator));
        }
    }

    public void onStop() {
        if(ByteCloud.getInstance().getDatabaseServer().existsServer(this.serverId)) {
            ByteCloud.getInstance().getDatabaseServer().removeServer(this.serverId);
        }
        if(isRunning()) {
            this.process.destroy();
            try {
                FileUtils.copyFile(new File(this.getDirectory(), "/logs/latest.log"), new File(EnumFile.SERVERS_LOGS.getPath(), this.getServerId()+".log"));
            } catch (IOException e) {
                e.printStackTrace();
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

    public boolean isRunning() {
        return this.process != null && this.process.isAlive();
    }

}
