package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.cloud.packet.*;
import de.bytelist.bytecloud.server.group.ServerGroup;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TempServer extends Server {

    @Getter
    private ServerGroup serverGroup;

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public TempServer(String serverId, int port, int ramM, int maxPlayer, int maxSpectator, ServerGroup serverGroup) {
        super(serverId, port, ramM, maxPlayer, maxSpectator, ServerState.STARTING, EnumFile.SERVERS_RUNNING.getPath());
        this.serverGroup = serverGroup;

        try {
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "spigot"), this.getDirectory());
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "plugins"), new File(this.getDirectory(), "plugins"));
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "cloud.properties"), new File(this.getDirectory(), "plugins/ByteCloud/cloud.properties"));
            FileUtils.copyDirectory(new File(EnumFile.TEMPLATES.getPath(), serverGroup.getGroupName()), this.getDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean startServer(String sender) {
        this.starter = sender;
        return byteCloud.getCloudExecutor().execute(() -> {
            if (!sender.equals("_cloud")) {
                PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Starting server §e" + getServerId() + "§7.");
                byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
            }
            if (process == null) {
                byteCloud.getLogger().info("Server " + serverId + " is starting on port " + port + ".");
                byteCloud.getServerHandler().registerServer(this);
                String[] param =
                        {"java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-Xmn2M", "-Xmx" + ramM + "M", "-Dde.bytelist.bytecloud.servername=" + serverId, "-Dfile.encoding=UTF-8", "-Dcom.mojang.eula.agree=true",
                                "-jar", "spigot-" + byteCloud.getCloudProperties().getProperty("spigot-version") + ".jar", "-s",
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
        });
    }

    @Override
    public boolean stopServer(String sender) {
        this.stopper = sender;
        return byteCloud.getCloudExecutor().execute(() -> {
            if (!sender.equals("_cloud")) {
                PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Stopping server §e" + getServerId() + "§7.");
                byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
            }
            if (this.process != null) {
                byteCloud.getLogger().info("Server " + serverId + " is stopping.");
                if (this.process.isAlive()) {
                    PacketOutMovePlayer packetOutMovePlayer = new PacketOutMovePlayer(byteCloud.getServerHandler().getRandomLobbyId(), "§6Verbinde zur Lobby...");
                    byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutMovePlayer);
                    while (true) {
                        if(byteCloud.getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt() == 0 &&
                                byteCloud.getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt() == 0)
                            break;
                        else {
                            try {
                                Thread.sleep(2000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        this.process.getOutputStream().write("stop\n".getBytes());
                        this.process.getOutputStream().flush();
                        Thread.sleep(1000L);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                this.process.destroy();

                try {
                    FileUtils.copyFile(new File(this.getDirectory(), "/logs/latest.log"), new File(EnumFile.SERVERS_LOGS.getPath(), this.getServerId() + ".log"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    FileUtils.deleteDirectory(this.directory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            byteCloud.getDatabaseServer().removeServer(this.serverId);
            byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), new PacketOutUnregisterServer(serverId));
            byteCloud.getServerHandler().unregisterServer(this);

            if (!stopper.equals("_cloud")) {
                PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(stopper, "§aServer §e" + getServerId() + "§a stopped.");
                byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
            }

            byteCloud.getLogger().info("Server " + serverId + " stopped.");
        });
    }

    @Override
    public void onStart() {
        if (this.process != null) {
            byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), new PacketOutRegisterServer(serverId, port));
            byteCloud.getCloudServer().sendPacket(serverId, new PacketOutCloudInfo(byteCloud.getVersion(), byteCloud.getCloudStarted(), byteCloud.isRunning));
            byteCloud.getLogger().info("Server " + serverId + " started. RAM: " + this.ramM + " Slots: " + (this.maxPlayer + this.maxSpectator));
        }
        byteCloud.getDatabaseServer().addServer(this.serverGroup.getGroupName(), this.getServerId(),
                this.getPort(), this.getServerState().name(), this.getMaxPlayer(), this.getMaxSpectator(), "Starting", null);
        if (!starter.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(starter, "§aServer §e" + getServerId() + "§a started.");
            byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
        }
    }

    @Override
    public void setServerState(ServerState serverState) {
        ServerGroup serverGroup = byteCloud.getServerHandler().getServerGroups().getOrDefault("LOBBY", null);
        if (serverGroup != null) {
            PacketOutChangeServerState packetOutChangeServerState = new PacketOutChangeServerState(this.getServerId(), this.serverGroup.getGroupName(), getServerState().name(), serverState.name());
            for (String server : serverGroup.getServers()) {
                byteCloud.getCloudServer().sendPacket(server, packetOutChangeServerState);
            }
        }
        super.setServerState(serverState);
    }
}
