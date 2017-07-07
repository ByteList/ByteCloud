package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutSendMessage;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ByteList on 28.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class PermServer extends Server {

    @Getter
    private PermServerObject permServerObject;
    private String starter;
    private String stopper;

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public PermServer(String serverId, PermServerObject permServerObject) {
        super(serverId, permServerObject.get("port").getAsInt(), ServerState.STARTING, EnumFile.SERVERS_PERMANENT.getPath());
        this.permServerObject = permServerObject;

        try {
            FileUtils.copyDirectory(new File(EnumFile.GENERALS.getPath(), "plugins"), new File(this.getDirectory(), "plugins"));
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "cloud.properties"), new File(this.getDirectory(), "plugins/ByteCloud/cloud.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer(String sender, int ramM, int maxPlayer, int maxSpectator) {
        super.startServer(ramM, maxPlayer, maxSpectator);
        this.starter = sender;
        if(!sender.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Starting server §e"+getServerId()+"§7.");
            byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
    }

    public void stopServer(String sender) {
        this.stopper = sender;
        if(!sender.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§7Stopping server §e"+getServerId()+"§7.");
            byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
        super.stopServer();
    }

    @Override
    public void onStart() {
        super.onStart();
        ByteCloud.getInstance().getDatabaseServer().addServer("PERMANENT", this.getServerId(),
                this.getPort(), this.getServerState().name(), this.getMaxPlayer(), this.getMaxSpectator(), "Permanent-Server", null);
        if(!starter.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(starter, "§aServer §e"+getServerId()+"§a started.");
            byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ByteCloud.getInstance().getServerHandler().removePermanentServer(this);
        if(!stopper.equals("_cloud")) {
            PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(stopper, "§aServer §e"+getServerId()+"§a stopped.");
            byteCloud.getCloudServer().sendPacket(ByteCloud.getInstance().getBungee().getBungeeId(), packetOutSendMessage);
        }
    }
}
