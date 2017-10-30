package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutCloudInfo;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutRegisterServer;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutSendMessage;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutUnregisterServer;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PermServer extends Server {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public PermServer(String serverId, int port, int ramM, int maxPlayer, int maxSpectator) {
        super(serverId, port, ramM, maxPlayer, maxSpectator, ServerState.STARTING, EnumFile.SERVERS_PERMANENT.getPath());

        try {
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "plugins"), new File(this.getDirectory(), "plugins"));
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "cloud.properties"), new File(this.getDirectory(), "plugins/ByteCloud/cloud.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean startServer(String sender) {
        this.starter = sender;
        if(!sender.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Starting server §e"+getServerId()+"§7.");
            byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
        if (process == null) {
            byteCloud.getLogger().info("Server " + serverId + " (permanent) is starting on port " + port + ".");
            byteCloud.getServerHandler().registerServer(this);
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
            return true;
        }
        return false;
    }

    @Override
    public boolean stopServer(String sender) {
        this.stopper = sender;
        if(!sender.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Stopping permanent server §e"+getServerId()+"§7.");
            byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
        if(this.process != null) {
            byteCloud.getLogger().info("Server " + serverId + " (permanent) is stopping.");
            if(this.process.isAlive()) {
                try {
                    this.process.getOutputStream().write("stop\n".getBytes());
                    this.process.getOutputStream().flush();
                    Thread.sleep(1500L);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            this.process.destroy();
        }

        byteCloud.getDatabaseServer().removeServer(this.serverId);

        byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), new PacketOutUnregisterServer(serverId));
        byteCloud.getServerHandler().unregisterServer(this);

        if(!stopper.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(stopper, "§aServer §e"+getServerId()+"§a stopped.");
            byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }

        byteCloud.getLogger().info("Server " + serverId + " (permanent) stopped.");
        return false;
    }

    @Override
    public void onStart() {
        if(this.process != null) {
            byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), new PacketOutRegisterServer(serverId, port));
            byteCloud.getCloudServer().sendPacket(serverId, new PacketOutCloudInfo(byteCloud.getVersion(), byteCloud.getCloudStarted(), byteCloud.isRunning));
            byteCloud.getLogger().info("Server " + serverId + " (permanent) started. RAM: "+this.ramM+" Slots: "+(this.maxPlayer+this.maxSpectator));
        }
        byteCloud.getDatabaseServer().addServer("PERMANENT", this.getServerId(),
                this.getPort(), this.getServerState().name(), this.getMaxPlayer(), this.getMaxSpectator(), "Permanent-Server", null);
        if(!starter.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(starter, "§aServer §e"+getServerId()+"§a started.");
            byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
    }


}
