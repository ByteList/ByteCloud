package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStartedPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStoppedPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerMessagePacket;
import de.bytelist.bytecloud.file.EnumFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
                    byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(sender),
                            Cloud.PREFIX+"§7Starting server §e" + getServerId() + "§7 (permanent)."));
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
                    byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(sender),
                            Cloud.PREFIX + "§cToo much servers are currently online!"));
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
                        Cloud.PREFIX + "§7Stopping server §e" + getServerId() + "§7 (permanent)."));
            }
            setServerState(ServerState.STOPPED);
            if(this.process != null) {
                super.manageProcessOnStop();
            }

            byteCloud.sendGlobalPacket(new CloudServerStoppedPacket(this.serverId, "Server stopped."));
            byteCloud.getServerHandler().unregisterServer(this);

            if (!stopper.equals("_cloud")) {
                byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(sender),
                        Cloud.PREFIX + "§aServer §e" + getServerId() + "§a (permanent) stopped."));
            }

            byteCloud.getLogger().info("Server " + serverId + " (permanent) stopped.");
        });

        if(!b) byteCloud.getLogger().warning("CloudExecutor returns negative statement while stopping cloud "+serverId);
        return b;
    }

    @Override
    public void onStart() {
        if(this.process != null) {
            super.onStart();
            byteCloud.sendGlobalPacket(new CloudServerStartedPacket(this.serverId, this.port, "{null}", this.serverPermanent,
                    this.slots, this.motd));
            byteCloud.getLogger().info("Server " + serverId + " (permanent) started. RAM: "+this.ramM+" Slots: "+this.slots);
            this.started = true;
        }
        if (!starter.equals("_cloud")) {
            byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(UUID.fromString(starter),
                    Cloud.PREFIX + "§aServer §e" + getServerId() + "§a started."));
        }
    }
}
