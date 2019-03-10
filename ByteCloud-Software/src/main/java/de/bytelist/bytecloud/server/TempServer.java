package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerGroupInfoPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStartedPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStoppedPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerConnectPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerKickPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerMessagePacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerServerSwitchPacket;
import de.bytelist.bytecloud.file.EnumFile;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TempServer extends Server {

    @Getter
    private ServerGroup serverGroup;

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    TempServer(String serverId, int port, int ramM, int slots, ServerGroup serverGroup) {
        super(serverId, port, ramM, slots, ServerState.STARTING, EnumFile.SERVERS_RUNNING.getPath());
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
            byteCloud.debug(toString() + " - start - begin ===========");
            if (!byteCloud.isRunning) return;
            setServerState(ServerState.STARTING);
            if (!sender.equals("_cloud")) {
                byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(sender),
                        "§7Starting server §e" + getServerId() + "§7."));
            }
            byteCloud.debug(toString() + " - start - process ?0 -> " + (process != null));
            if (process == null) {
                byteCloud.getLogger().info("Server " + serverId + " is starting on port " + port + ".");
                byteCloud.getServerHandler().registerServer(this);
                String[] param = {"java",
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
                        "-Dde.bytelist.bytecloud.servergroup=" + serverGroup.getGroupName(),
                        "-Dde.bytelist.bytecloud.communication=" + byteCloud.getPacketEncryptionKey(),

                        "-Xmx" + ramM + "M",
                        "-jar", byteCloud.getCloudConfig().getString("jar-name") + ".jar",

                        "-s", String.valueOf(slots),
                        "-o", "false",
                        "-p", String.valueOf(port),
                        "nogui"
                };
                byteCloud.debug(toString() + " - start - param: " + (Arrays.toString(param)));
                ProcessBuilder pb = new ProcessBuilder(param);
                pb.directory(directory);
                try {
                    process = pb.start();
                    byteCloud.getServerHandler().setAreServersRunning();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byteCloud.debug(toString() + " - start - process ?0 -> " + (process != null));
            }
            byteCloud.debug(toString() + " - start - end ===========");
        });

        if (!b)
            byteCloud.getLogger().warning("CloudExecutor returns negative statement while starting cloud " + serverId);
        return b;
    }

    @Override
    public boolean stopServer(String sender) {
        this.stopper = sender;
        boolean b = byteCloud.getCloudExecutor().execute(() -> {
            byteCloud.getLogger().info("Server " + serverId + " is stopping.");
            if (!sender.equals("_cloud")) {
                byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(sender),
                        "§7Stopping server §e" + getServerId() + "§7."));
            }
            setServerState(ServerState.STOPPED);
            if (this.process != null) {
                super.manageProcessOnStop();

                try {
                    FileUtils.copyFile(new File(this.getDirectory(), "/logs/latest.log"), new File(EnumFile.SERVERS_LOGS.getPath(),
                            this.getServerId() + ".log"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    FileUtils.deleteDirectory(this.directory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            byteCloud.sendGlobalPacket(new CloudServerStoppedPacket(this.serverId, "Server stopped."));
            byteCloud.getServerHandler().unregisterServer(this);

            if (!stopper.equals("_cloud")) {
                byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(sender),
                        "§aServer §e" + getServerId() + "§a stopped."));
            }

            byteCloud.getLogger().info("Server " + serverId + " stopped.");
        });
        if (!b)
            byteCloud.getLogger().warning("CloudExecutor returns negative statement while stopping cloud " + serverId);
        return b;
    }

    @Override
    public void onStart() {
        if (this.process != null) {
            super.onStart();

            byteCloud.sendGlobalPacket(new CloudServerStartedPacket(this.serverId, this.port, this.serverGroup.getGroupName(), this.serverPermanent,
                    this.slots, this.motd));
            byteCloud.getLogger().info("Server " + serverId + " started. RAM: " + this.ramM + " Slots: " + this.slots);
            this.started = true;
        }
        if (!starter.equals("_cloud")) {
            byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(starter),
                    "§aServer §e" + getServerId() + "§a started."));
        }
    }
}
