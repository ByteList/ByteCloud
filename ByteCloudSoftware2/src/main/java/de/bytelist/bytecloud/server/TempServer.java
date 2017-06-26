package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutSendMessage;
import de.bytelist.bytecloud.server.group.ServerGroup;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TempServer extends Server {

    private ServerGroup serverGroup;
    private String starter;
    private String stopper;

    public TempServer(String serverId, int port, ServerGroup serverGroup) {
        super(serverId, port, ServerState.STARTING, EnumFile.SERVERS_RUNNING.getPath());
        this.serverGroup = serverGroup;

        try {
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "spigot"), this.getDirectory());
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "plugins"), new File(this.getDirectory(), "plugins"));
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "cloud.properties"), new File(this.getDirectory(), "plugins/ByteCloud/cloud.properties"));
            FileUtils.copyDirectory(new File(EnumFile.TEMPLATES.getPath(), serverGroup.getName()), this.getDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer(String sender, int ramM, int maxPlayer, int maxSpectator) {
        super.startServer(ramM, maxPlayer, maxSpectator);
        this.starter = sender;
        if(!sender.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Starting server §e"+getServerId()+"§7.");
            NetworkManager.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
    }

    public void stopServer(String sender, StopType stopType) {
        if(this.serverGroup.getName().equals("LOBBY") && this.serverGroup.getServers().size() < 1) {
            if(ByteCloud.getInstance().getServerHandler().existsPermanentServer(""))
            stopType = StopType.KICK;
            SERVER_KICK_MESSAGE = "§6Du wurdest vom Netzwerk gekickt, da kein Lobby-Server verfügbar ist.";
        }
        this.stopper = sender;
        if(!sender.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Stopping server §e"+getServerId()+"§7.");
            NetworkManager.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
        super.stopServer(stopType);
        try {
            FileUtils.deleteDirectory(this.getDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ByteCloud.getInstance().getDatabaseServer().addServer(this.serverGroup.getName(), this.getServerId(),
                this.getPort(), this.getServerState().name(), this.getMaxPlayer(), this.getMaxSpectator(), "Starting", null);
        if(!starter.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(starter, "§aServer §e"+getServerId()+"§a started.");
            NetworkManager.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        this.serverGroup.removeServer(this);
        try {
            Thread.sleep(2200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.copyFile(new File(this.getDirectory(), "/logs/latest.log"), new File(EnumFile.SERVERS_LOGS.getPath(), this.getServerId()+".log"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileUtils.deleteDirectory(this.getDirectory());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!stopper.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(stopper, "§aServer §e"+getServerId()+"§a stopped.");
            NetworkManager.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
    }
}
