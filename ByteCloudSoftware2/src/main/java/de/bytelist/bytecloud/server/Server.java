package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutCloudInfo;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutRegisterServer;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutUnregisterServer;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;

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
    @Getter @Setter
    private ServerState serverState;
    @Getter
    private final File directory;
    @Getter
    private Process process;

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public Server(String serverId, int port, ServerState serverState, String directory) {
        this.serverId = serverId;
        this.port = port;
        this.serverState = serverState;
        this.directory = new File(directory, serverId);
    }

    public void startServer(int ramM, int maxPlayer, int maxSpectator) {
        this.ramM = ramM;
        this.maxPlayer = maxPlayer;
        this.maxSpectator = maxSpectator;
        if (process == null) {
            byteCloud.getLogger().info("Server " + serverId + " is starting on port " + port + ".");
            String[] param =
                    { "java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-Xmn2M", "-Xmx" + ramM + "M", "-Dde.bytelist.bytecloud.servername="+serverId, "-Dfile.encoding=UTF-8", "-Dcom.mojang.eula.agree=true",
                            "-jar", "spigot-"+ byteCloud.getCloudProperties().getProperty("spigot-version")+".jar", "-s",
                            String.valueOf((maxPlayer + maxSpectator)), "-o", "false", "-p", String.valueOf(port), "nogui"};
            ProcessBuilder pb = new ProcessBuilder(param);
            pb.directory(directory);
            try {
                process = pb.start();
                byteCloud.getServerHandler().setAreServersRunning();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer() {
        if(this.process != null) {
            if(this.process.isAlive()) {
                byteCloud.getLogger().info("Server " + serverId + " is stopping.");

//                PacketOutKickAllPlayers packetOutKickAllPlayers = new PacketOutKickAllPlayers("§6Du wurdest auf die Lobby verschoben.");
//                byteCloud.getCloudServer().sendPacket(this.serverId, packetOutKickAllPlayers);
                try {
                    Thread.sleep(3000L);
                    this.process.getOutputStream().write("stop\n".getBytes());
                    this.process.getOutputStream().flush();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
//      byteCloud.getLogger().info("Server " + serverId + " is stopping.");

        byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), new PacketOutUnregisterServer(serverId));
    }

    public void onStart() {
        if(this.process != null) {
            byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), new PacketOutRegisterServer(serverId, port));
            byteCloud.getCloudServer().sendPacket(serverId, new PacketOutCloudInfo(byteCloud.getVersion(), byteCloud.getCloudStarted(), byteCloud.isRunning));
            byteCloud.getLogger().info("Server " + serverId + " started. RAM: "+this.ramM+" Slots: "+(this.maxPlayer+this.maxSpectator));
        }
    }

    public void onStop() {
        if(byteCloud.getDatabaseServer().existsServer(this.serverId)) {
            byteCloud.getDatabaseServer().removeServer(this.serverId);
        }
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(isRunning()) {
            this.process.destroy();
            this.process = null;
        }
        byteCloud.getLogger().info("Server " + serverId + " stopped.");
    }

    public boolean isRunning() {
        return this.process != null && this.process.isAlive();
    }

    public enum ServerState {
        STARTING,
        LOBBY,
        FULL,
        INGAME,
        RESTART,
        STOPPED
    }
}
