package de.bytelist.bytecloud.network.cloud;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.server.JsonServerListener;
import com.voxelboxstudios.resilent.server.Patron;
import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.PacketName;
import de.bytelist.bytecloud.server.Server;
import de.bytelist.bytecloud.server.ServerGroup;

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
            PacketName packet = PacketName.getPacketName(jsonObject.get("packet").getAsString());
            String group, serverId, sender;

            switch (packet) {
                case IN_SERVER:
                    serverId = jsonObject.get("serverId").getAsString();
                    byteCloud.getCloudServer().registerClient(serverId, patron);
                    byteCloud.getServerHandler().getServer(serverId).onStart();
                    break;
                case NULL:
                    break;
                case IN_BUNGEE:
                    String bungeeId = jsonObject.get("bungeeId").getAsString();
                    byteCloud.getCloudServer().registerClient(bungeeId, patron);
                    byteCloud.getBungee().onStart();
                    break;
                case IN_BUNGEE_STPOPPED:
                    byteCloud.getBungee().onStop();
                    break;
                case IN_START_SERVER:
                    group = jsonObject.get("group").getAsString();
                    sender = jsonObject.get("sender").getAsString();

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
                    break;
                case IN_STOP_SERVER:
                    serverId = jsonObject.get("serverId").getAsString();
                    sender = jsonObject.get("sender").getAsString();

                    if(byteCloud.getServerHandler().existsServer(serverId)) {
                        Server server = byteCloud.getServerHandler().getServer(serverId);
                        server.stopServer(sender);
                    } else {
                        PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§cThis server does not exist.");
                        byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), packetOutSendMessage);
                    }
                    break;
                case IN_CHANGE_SERVER_STATE:
                    serverId = jsonObject.get("serverId").getAsString();
                    if(byteCloud.getServerHandler().existsServer(serverId)) {
                        Server server = byteCloud.getServerHandler().getServer(serverId);
                        server.setServerState(Server.ServerState.valueOf(jsonObject.get("serverState").getAsString()));
                    }
                    break;
                case IN_KICK_PLAYER:
                    break;
                case IN_STOP_OWN_SERVER:
                    serverId = jsonObject.get("serverId").getAsString();
                    if(byteCloud.getServerHandler().existsServer(serverId)) {
                        Server server = byteCloud.getServerHandler().getServer(serverId);
                        server.stopServer("_cloud");
                    }
                    break;
                case OUT_CHANGE_SERVER_STATE:
                    break;
                case OUT_CLOUD_INFO:
                    break;
                case OUT_EXECUTE_COMMAND:
                    break;
                case OUT_KICK_ALL_PLAYERS:
                    break;
                case OUT_KICK_PLAYER:
                    break;
                case OUT_MOVE_PLAYER:
                    break;
                case OUT_REGISTER_PLAYER:
                    break;
                case OUT_SEND_MESSAGE:
                    break;
                case OUT_UNREGISTER_SERVER:
                    break;
            }
        }
    }

    @Override
    public void connected(Patron patron) {
        NetworkManager.getLogger().info("Connection "+patron.getInetAddress().getHostAddress()+":"+patron.getSocket().getPort()+" ("+patron.getID()+") connected.");
    }

    @Override
    public void disconnected(Patron patron) {
        if(byteCloud.getCloudServer().isClient(patron)) {
            byteCloud.getCloudServer().unregisterClient(patron);
        } else {
            NetworkManager.getLogger().warning("Connection "+patron.getID()+" disconnected. Wasn't a client!");
        }
    }
}
