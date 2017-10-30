package de.bytelist.bytecloud.network.cloud;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.server.JsonServerListener;
import com.voxelboxstudios.resilent.server.Patron;
import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.bungee.packet.PacketInBungee;
import de.bytelist.bytecloud.network.bungee.packet.PacketInBungeeStopped;
import de.bytelist.bytecloud.network.bungee.packet.PacketInStartServer;
import de.bytelist.bytecloud.network.bungee.packet.PacketInStopServer;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutSendMessage;
import de.bytelist.bytecloud.network.server.packet.PacketInChangeServerState;
import de.bytelist.bytecloud.network.server.packet.PacketInServer;
import de.bytelist.bytecloud.network.server.packet.PacketInStopOwnServer;
import de.bytelist.bytecloud.server.Server;
import de.bytelist.bytecloud.server.group.ServerGroup;

/**
 * Created by ByteList on 26.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudServerListener extends JsonServerListener {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    @Override
    public void jsonReceived(Patron patron, JsonObject jsonObject) {
        if(jsonObject.has("packet")) {
            String packet = jsonObject.get("packet").getAsString();
            if(packet.equals(PacketInServer.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString();
                byteCloud.getCloudServer().registerClient(serverId, patron);
                byteCloud.getServerHandler().getServer(serverId).onStart();
            }
            if(packet.equals(PacketInBungee.class.getSimpleName())) {
                String bungeeId = jsonObject.get("bungeeId").getAsString();
                byteCloud.getCloudServer().registerClient(bungeeId, patron);
                byteCloud.getBungee().onStart();
            }
            if(packet.equals(PacketInBungeeStopped.class.getSimpleName())) {
                byteCloud.getBungee().onStop();
            }
            if(packet.equals(PacketInStartServer.class.getSimpleName())) {
                String group = jsonObject.get("group").getAsString();
                String sender = jsonObject.get("sender").getAsString();

                ServerGroup serverGroup = null;
                for (ServerGroup sg : byteCloud.getServerHandler().getServerGroups().values()) {
                    if (sg.getGroupName().equalsIgnoreCase(group)) {
                        serverGroup = sg;
                        break;
                    }
                }
                if(serverGroup != null) {
                    serverGroup.startNewServer(sender);
                } else {
                    PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§cThis server group does not exist.");
                    byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
                }
            }
            if(packet.equals(PacketInStopServer.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString();
                String sender = jsonObject.get("sender").getAsString();

                if(byteCloud.getServerHandler().existsServer(serverId)) {
                    Server server = byteCloud.getServerHandler().getServer(serverId);
                    server.stopServer(sender);
                } else {
                    PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§cThis server does not exist.");
                    byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
                }
            }
            if(packet.equals(PacketInStopOwnServer.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString();
                if(byteCloud.getServerHandler().existsServer(serverId)) {
                    Server server = byteCloud.getServerHandler().getServer(serverId);
                    server.stopServer("_cloud");
                }
            }

            if(packet.equals(PacketInChangeServerState.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString();
                if(byteCloud.getServerHandler().existsServer(serverId)) {
                    Server server = byteCloud.getServerHandler().getServer(serverId);
                    server.setServerState(Server.ServerState.valueOf(jsonObject.get("serverState").getAsString()));
                }
            }
        }
    }

    @Override
    public void connected(Patron patron) {
        NetworkManager.getLogger().info("Client "+patron.getID()+" connected. Waiting for response...");
    }

    @Override
    public void disconnected(Patron patron) {
        byteCloud.getCloudServer().unregisterClient(patron);
    }
}
