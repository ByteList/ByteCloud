package de.bytelist.bytecloud.network.bungee;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.client.JsonClientListener;
import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.bungee.cloud.CloudHandler;
import de.bytelist.bytecloud.network.cloud.packet.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

/**
 * Created by ByteList on 26.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class BungeeClientListener extends JsonClientListener {

    private final ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();

    @Override
    public void jsonReceived(JsonObject jsonObject) {
        if(jsonObject.has("packet")) {
            String packet = jsonObject.get("packet").getAsString();
            if(packet.equals(PacketOutKickAllPlayers.class.getSimpleName())) {
                String reason = jsonObject.get("reason").getAsString();
                for(ProxiedPlayer player : byteCloudMaster.getProxy().getPlayers()) {
                    player.disconnect(reason.replace("#&C#", "§"));
                }
            }
            if(packet.equals(PacketOutRegisterServer.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString();
                int port = jsonObject.get("port").getAsInt();
                byteCloudMaster.getProxy().getServers().put(serverId, byteCloudMaster.getProxy().constructServerInfo(serverId, new InetSocketAddress("localhost", port), "ByteCloud Minecraft-Server", false));
            }
            if(packet.equals(PacketOutUnregisterServer.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString();
                if(byteCloudMaster.getProxy().getServers().containsKey(serverId))
                    byteCloudMaster.getProxy().getServers().remove(serverId);
            }

            if(packet.equals(PacketOutMovePlayer.class.getSimpleName())) {
                String toMoveServerId = jsonObject.get("serverId").getAsString();
                String reason = jsonObject.get("reason").getAsString();
                String players = jsonObject.get("players").getAsString();

                if(players.equals("_all")) {
                    for(ProxiedPlayer pp : byteCloudMaster.getProxy().getPlayers()) {
                        pp.sendMessage("§c"+reason.replace("#&C#", "§"));
                        pp.connect(byteCloudMaster.getProxy().getServerInfo(toMoveServerId));
                    }
                } else {
                    if(players.contains("#")) {
                        for (String player : players.split("#")) {
                            ProxiedPlayer pp = byteCloudMaster.getProxy().getPlayer(player);
                            if (pp != null) {
                                pp.sendMessage("§c" + reason);
                                pp.connect(byteCloudMaster.getProxy().getServerInfo(toMoveServerId));
                            }
                        }
                    } else {
                        ProxiedPlayer pp = byteCloudMaster.getProxy().getPlayer(players);
                        if (pp != null) {
                            pp.sendMessage("§c" + reason);
                            pp.connect(byteCloudMaster.getProxy().getServerInfo(toMoveServerId));
                        }
                    }
                }
            }

            if(packet.equals(PacketOutSendMessage.class.getSimpleName())) {
                String player = jsonObject.get("player").getAsString();
                String message = jsonObject.get("message").getAsString().replace("#&C#", "§");
                ProxiedPlayer pp = byteCloudMaster.getProxy().getPlayer(player);
                if(pp != null)
                    pp.sendMessage(byteCloudMaster.prefix+"§r"+message);
            }

            if(packet.equals(PacketOutCloudInfo.class.getSimpleName())) {
                CloudHandler cloudHandler = byteCloudMaster.getCloudHandler();
                cloudHandler.setCloudVersion(jsonObject.get("cloudVersion").getAsString());
                cloudHandler.setCloudStarted(jsonObject.get("cloudStarted").getAsString());
                cloudHandler.setCloudRunning(jsonObject.get("cloudRunning").getAsBoolean());
            }

            if(packet.equals(PacketOutExecuteCommand.class.getSimpleName())) {
                String cmd = jsonObject.get("command").getAsString();
                byteCloudMaster.getLogger().info("Execute cmd from cloud: "+cmd);
                byteCloudMaster.getProxy().getPluginManager().dispatchCommand(byteCloudMaster.getProxy().getConsole(), cmd);
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
