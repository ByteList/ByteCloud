package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStartedPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStoppedPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerKickPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerMessagePacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerServerSwitchPacket;
import de.bytelist.bytecloud.file.EnumFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PermServer extends Server {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public PermServer(String serverId, int port, int ramM, int slots) {
        super(serverId, port, ramM, slots, ServerState.STARTING, EnumFile.SERVERS_PERMANENT.getPath());
        this.serverPermanent = true;

        try {
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "plugins"), new File(this.getDirectory(), "plugins"));
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "config.json"), new File(this.getDirectory(), "plugins/ByteCloud/config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean startServer(String sender) {
        this.starter = sender;
        boolean b = byteCloud.getCloudExecutor().execute(()-> {
            if(byteCloud.getUsedMemory()+ramM < byteCloud.getMaxMemory()) {
                if(!byteCloud.isRunning) return;
                setServerState(ServerState.STARTING);
                if (!sender.equals("_cloud")) {
//                    PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Starting cloud §e" + getServerId() + "§7.");
//                    byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
                }
                if (process == null) {
                    byteCloud.getLogger().info("Server " + serverId + " (permanent) is starting on port " + port + ".");
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
                            "-Dde.bytelist.bytecloud.servergroup=PERMANENT",
                            "-Dde.bytelist.bytecloud.communication="+byteCloud.getPacketEncryptionKey(),

                            "-Xmx" + ramM + "M",
                            "-jar", byteCloud.getCloudConfig().getString("jar-name") + ".jar",

                            "-s", String.valueOf(slots),
                            "-o", "false",
                            "-p", String.valueOf(port),
                            "nogui"
                    };
                    ProcessBuilder pb = new ProcessBuilder(param);
                    pb.directory(directory);
                    try {
                        process = pb.start();
                        byteCloud.getServerHandler().setAreServersRunning();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if (!sender.equals("_cloud")) {
//                    PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§cToo much servers are currently online!");
//                    byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
                } else {
                    byteCloud.getLogger().info("Server " + serverId + " can't start! Too much servers are currently online!");
                }
            }
        });

        if(!b) byteCloud.getLogger().warning("CloudExecutor returns negative statement while starting cloud "+serverId);
        return b;
    }

    @Override
    public boolean stopServer(String sender) {
        this.stopper = sender;
        boolean b = byteCloud.getCloudExecutor().execute(()-> {
            byteCloud.getLogger().info("Server " + serverId + " (permanent) is stopping.");
            if (!sender.equals("_cloud")) {
                byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(sender),
                        "§7Stopping cloud §e" + getServerId() + "§7."));
            }
            setServerState(ServerState.STOPPED);
            if(this.process != null) {
                if(this.process.isAlive()) {
                    List<CloudPlayer> cloudPlayers = new ArrayList<>(this.players);

                    if (byteCloud.isRunning) {
                        String lobbyId = byteCloud.getServerHandler().getRandomLobbyId(serverId);

                        if (!byteCloud.getServerIdOnConnect().equals(this.serverId)) {
                            cloudPlayers.forEach(cloudPlayer -> {
                                byteCloud.sendGlobalPacket(new CloudPlayerServerSwitchPacket(cloudPlayer.getUuid(), lobbyId));
                                byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(cloudPlayer.getUuid(),
                                        "§6Verbinde zur Lobby..."));
                            });
                        } else {
                            cloudPlayers.forEach(cloudPlayer -> {
                                byteCloud.sendGlobalPacket(new CloudPlayerKickPacket(cloudPlayer.getUuid(),
                                        "§7Server stopped.\n§cDu konntest nicht zur Lobby verbunden werden!"));
                            });
                        }
                    }
                    while (true) {
                        if (this.currentPlayers < 1)
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
                        Thread.sleep(1500L);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                byteCloud.getScreenManager().checkAndRemove(this);
                this.process.destroy();
            }

            byteCloud.sendGlobalPacket(new CloudServerStoppedPacket(this.serverId, "Server stopped."));
            byteCloud.getServerHandler().unregisterServer(this);

            if (!stopper.equals("_cloud")) {
                byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(sender),
                        "§aServer §e" + getServerId() + "§a stopped."));
            }

            byteCloud.getLogger().info("Server " + serverId + " (permanent) stopped.");
        });

        if(!b) byteCloud.getLogger().warning("CloudExecutor returns negative statement while stopping cloud "+serverId);
        return b;
    }

    @Override
    public void onStart() {
        if(this.process != null) {
            byteCloud.sendGlobalPacket(new CloudServerStartedPacket(this.serverId, this.port, "{null}", this.serverPermanent,
                    this.slots, this.motd));
            byteCloud.getLogger().info("Server " + serverId + " (permanent) started. RAM: "+this.ramM+" Slots: "+this.slots);
        }
        if (!starter.equals("_cloud")) {
            byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(starter),
                    "§aServer §e" + getServerId() + "§a started."));
        }
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
