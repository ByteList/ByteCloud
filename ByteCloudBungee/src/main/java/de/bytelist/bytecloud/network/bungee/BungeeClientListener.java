package de.bytelist.bytecloud.network.bungee;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.client.JsonClientListener;
import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.bungee.cloud.CloudHandler;
import de.bytelist.bytecloud.network.cloud.packet.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

/**
 * Created by ByteList on 26.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BungeeClientListener extends JsonClientListener {
    @Override
    public void jsonReceived(JsonObject jsonObject) {
        if(jsonObject.has("packet")) {
            String packet = jsonObject.get("packet").getAsString();
            if(packet.equals(PacketOutKickAllPlayers.class.getSimpleName())) {
                String reason = jsonObject.get("reason").getAsString();
                for(ProxiedPlayer player : ByteCloudMaster.getInstance().getProxy().getPlayers()) {
                    player.disconnect(reason);
                }
            }
            if(packet.equals(PacketOutRegisterServer.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString();
                int port = jsonObject.get("port").getAsInt();
                ServerInfo serverInfo = ByteCloudMaster.getInstance().getProxy().constructServerInfo(serverId, InetSocketAddress.createUnresolved("localhost", port), "", false);
                ByteCloudMaster.getInstance().getProxy().getServers().put(serverId, serverInfo);
            }
            if(packet.equals(PacketOutUnregisterServer.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString();
                if(ByteCloudMaster.getInstance().getProxy().getServers().containsKey(serverId))
                    ByteCloudMaster.getInstance().getProxy().getServers().remove(serverId);
            }

            if(packet.equals(PacketOutMovePlayer.class.getSimpleName())) {
                String toMoveServerId = jsonObject.get("serverId").getAsString();
                String reason = jsonObject.get("reason").getAsString();
                String players = jsonObject.get("players").getAsString();

                if(players.equals("_all")) {
                    for(ProxiedPlayer pp : ByteCloudMaster.getInstance().getProxy().getPlayers()) {
                        pp.sendMessage("§c"+reason);
                        pp.connect(ByteCloudMaster.getInstance().getProxy().getServerInfo(toMoveServerId));
                    }
                } else {
                    if(players.contains("#")) {
                        for (String player : players.split("#")) {
                            ProxiedPlayer pp = ByteCloudMaster.getInstance().getProxy().getPlayer(player);
                            if (pp != null) {
                                pp.sendMessage("§c" + reason);
                                pp.connect(ByteCloudMaster.getInstance().getProxy().getServerInfo(toMoveServerId));
                            }
                        }
                    } else {
                        ProxiedPlayer pp = ByteCloudMaster.getInstance().getProxy().getPlayer(players);
                        if (pp != null) {
                            pp.sendMessage("§c" + reason);
                            pp.connect(ByteCloudMaster.getInstance().getProxy().getServerInfo(toMoveServerId));
                        }
                    }
                }
            }

            if(packet.equals(PacketOutSendMessage.class.getSimpleName())) {
                String player = jsonObject.get("player").getAsString();
                String message = jsonObject.get("message").getAsString().replace("#&C#", "§");
                ProxiedPlayer pp = ByteCloudMaster.getInstance().getProxy().getPlayer(player);
                if(pp != null)
                    pp.sendMessage(ByteCloudMaster.getInstance().prefix+"§r"+message);
            }

            if(packet.equals(PacketOutCloudInfo.class.getSimpleName())) {
                CloudHandler cloudHandler = ByteCloudMaster.getInstance().getCloudHandler();
                cloudHandler.setCloudVersion(jsonObject.get("cloudVersion").getAsString());
                cloudHandler.setCloudStarted(jsonObject.get("cloudStarted").getAsString());
                cloudHandler.setCloudRunning(jsonObject.get("cloudRunning").getAsBoolean());
            }
        }
    }

    @Override
    public void disconnected() {
        System.out.println("["+this.getClass().getSimpleName()+"] Disconnected from Packet-Server.");
    }

    @Override
    public void connected() {
        System.out.println("["+this.getClass().getSimpleName()+"] Connected to Packet-Server.");
    }
}
