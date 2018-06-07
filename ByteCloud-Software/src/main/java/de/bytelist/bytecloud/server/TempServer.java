package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.cloud.*;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TempServer extends Server {

    @Getter
    private ServerGroup serverGroup;

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    TempServer(String serverId, int port, int ramM, int maxPlayer, int maxSpectator, ServerGroup serverGroup) {
        super(serverId, port, ramM, maxPlayer, maxSpectator, ServerState.STARTING, EnumFile.SERVERS_RUNNING.getPath());
        this.serverGroup = serverGroup;

        try {
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "spigot"), this.getDirectory());
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "plugins"), new File(this.getDirectory(), "plugins"));
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "config.json"), new File(this.getDirectory(), "plugins/ByteCloud/config.json"));
            FileUtils.copyDirectory(new File(EnumFile.TEMPLATES.getPath(), serverGroup.getGroupName().toUpperCase()), this.getDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean startServer(String sender) {
        this.starter = sender;
        boolean b = byteCloud.getCloudExecutor().execute(() -> {
            byteCloud.debug(toString()+" - start - begin ===========");
            if(!byteCloud.isRunning) return;

            if (!sender.equals("_cloud")) {
                PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Starting server §e" + getServerId() + "§7.");
                byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
            }
            byteCloud.debug(toString()+" - start - process ?0 -> "+(process != null));
            if (process == null) {
                byteCloud.getLogger().info("Server " + serverId + " is starting on port " + port + ".");
                byteCloud.getServerHandler().registerServer(this);
                String[] param = { "java",
                        "-XX:+UseG1GC",
                        "-XX:MaxGCPauseMillis=50",
                        "-XX:MaxPermSize=256M",
                        "-XX:-UseAdaptiveSizePolicy",
                        "-Dfile.encoding=UTF-8",
                        "-Dcom.mojang.eula.agree=true",
                        "-Dio.netty.recycler.maxCapacity=0",
                        "-Dio.netty.recycler.maxCapacity.default=0",
                        "-Djline.terminal=jline.UnsupportedTerminal",
                        "-Dde.bytelist.bytecloud.servername=" + serverId,
                        "-Dde.bytelist.bytecloud.servergroup="+serverGroup.getGroupName(),

                        "-Xmx" + ramM + "M",
                        "-jar", byteCloud.getCloudConfig().getString("jar-name") + ".jar",

                        "-s", String.valueOf((maxPlayer + maxSpectator)),
                        "-o", "false",
                        "-p", String.valueOf(port),
                        "nogui"
                        };
                byteCloud.debug(toString()+" - start - param: "+(Arrays.toString(param)));
                ProcessBuilder pb = new ProcessBuilder(param);
                pb.directory(directory);
                try {
                    process = pb.start();
                    byteCloud.getServerHandler().setAreServersRunning();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byteCloud.debug(toString()+" - start - process ?0 -> "+(process != null));
            }
            byteCloud.debug(toString()+" - start - end ===========");
        });

        if(!b) byteCloud.getLogger().warning("CloudExecutor returns negative statement while starting server "+serverId);
        return b;
    }

    @Override
    public boolean stopServer(String sender) {
        this.stopper = sender;
        boolean b = byteCloud.getCloudExecutor().execute(() -> {
            byteCloud.getLogger().info("Server " + serverId + " is stopping.");
            if (!sender.equals("_cloud")) {
                PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Stopping server §e" + getServerId() + "§7.");
                byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
            }
            if (this.process != null) {
                if (this.process.isAlive()) {
                    if(byteCloud.isRunning) {
                        ArrayList<String> player = new ArrayList<>();
                        Collections.addAll(player, byteCloud.getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PLAYERS).getAsString().split(","));
                        Collections.addAll(player, byteCloud.getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.SPECTATORS).getAsString().split(","));

                        if(!byteCloud.getServerIdOnConnect().equals(this.serverId)) {
                            PacketOutMovePlayer packetOutMovePlayer = new PacketOutMovePlayer(byteCloud.getServerHandler().getRandomLobbyId(serverId), "§6Verbinde zur Lobby...", player);
                            byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutMovePlayer);
                        } else {
                            PacketOutKickPlayer packetOutKickPlayer = new PacketOutKickPlayer("§7Server stopped.\n§cDu konntest nicht zur Lobby verbunden werden!", player);
                            byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutKickPlayer);
                        }
                    } else {
                        PacketOutKickAllPlayers packetOutKickPlayer = new PacketOutKickAllPlayers("§cDas Cloud-System wird gerade gestoppt.");
                        byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutKickPlayer);
                    }
                    while (true) {
                        if(byteCloud.getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt() < 1 &&
                                byteCloud.getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt() < 1)
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

                byteCloud.getScreenSystem().checkAndRemove(this);
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
        if(!b) byteCloud.getLogger().warning("CloudExecutor returns negative statement while stopping server "+serverId);
        return b;
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

    @Override
    public void runCommand(String command) {
        String x = command + "\n";
        if (this.process != null) {
            try {
                this.process.getOutputStream().write(x.getBytes());
                this.process.getOutputStream().flush();
            } catch (IOException var4) {
                var4.printStackTrace();
            }

        }
    }
}
