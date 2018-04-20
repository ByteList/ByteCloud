package de.bytelist.bytecloud.network.bungee;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.client.JsonClientListener;
import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.bungee.cloud.CloudHandler;
import de.bytelist.bytecloud.network.PacketName;
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
            PacketName packet = PacketName.getPacketName(jsonObject.get("packet").getAsString());
            String serverId, group, sender, reason, players;

            switch (packet) {
                case NULL:
                    break;
                case IN_BUNGEE:
                    break;
                case IN_BUNGEE_STPOPPED:
                    break;
                case IN_START_SERVER:
                    break;
                case IN_STOP_SERVER:
                    break;
                case IN_CHANGE_SERVER_STATE:
                    break;
                case IN_KICK_PLAYER:
                    break;
                case IN_SERVER:
                    break;
                case IN_STOP_OWN_SERVER:
                    break;
                case OUT_CHANGE_SERVER_STATE:
                    break;
                case OUT_CLOUD_INFO:
                    CloudHandler cloudHandler = byteCloudMaster.getCloudHandler();
                    cloudHandler.setCloudVersion(jsonObject.get("cloudVersion").getAsString());
                    cloudHandler.setCloudStarted(jsonObject.get("cloudStarted").getAsString());
                    cloudHandler.setCloudRunning(jsonObject.get("cloudRunning").getAsBoolean());
                    break;
                case OUT_EXECUTE_COMMAND:
                    String cmd = jsonObject.get("command").getAsString();
                    byteCloudMaster.getLogger().info("Execute cmd from cloud: "+cmd);
                    byteCloudMaster.getProxy().getPluginManager().dispatchCommand(byteCloudMaster.getProxy().getConsole(), cmd);
                    break;
                case OUT_KICK_ALL_PLAYERS:
                    reason = jsonObject.get("reason").getAsString();
                    for(ProxiedPlayer player : byteCloudMaster.getProxy().getPlayers()) {
                        player.disconnect(reason.replace("#&C#", "§"));
                    }
                    break;
                case OUT_KICK_PLAYER:
                    reason = jsonObject.get("reason").getAsString();
                    players = jsonObject.get("players").getAsString();

                    if(players.equals("_all")) {
                        for(ProxiedPlayer pp : byteCloudMaster.getProxy().getPlayers()) {
                            pp.disconnect(reason.replace("#&C#", "§"));
                        }
                    } else {
                        if(players.contains("#")) {
                            for (String player : players.split("#")) {
                                ProxiedPlayer pp = byteCloudMaster.getProxy().getPlayer(player);
                                if (pp != null) {
                                    pp.disconnect(reason.replace("#&C#", "§"));
                                }
                            }
                        } else {
                            ProxiedPlayer pp = byteCloudMaster.getProxy().getPlayer(players);
                            if (pp != null) {
                                pp.disconnect(reason.replace("#&C#", "§"));
                            }
                        }
                    }
                    break;
                case OUT_MOVE_PLAYER:
                    String toMoveServerId = jsonObject.get("serverId").getAsString();
                    reason = jsonObject.get("reason").getAsString();
                    players = jsonObject.get("players").getAsString();

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
                                    pp.sendMessage("§c"+reason.replace("#&C#", "§"));
                                    pp.connect(byteCloudMaster.getProxy().getServerInfo(toMoveServerId));
                                }
                            }
                        } else {
                            ProxiedPlayer pp = byteCloudMaster.getProxy().getPlayer(players);
                            if (pp != null) {
                                pp.sendMessage("§c"+reason.replace("#&C#", "§"));
                                pp.connect(byteCloudMaster.getProxy().getServerInfo(toMoveServerId));
                            }
                        }
                    }
                    break;
                case OUT_REGISTER_PLAYER:
                    serverId = jsonObject.get("serverId").getAsString();
                    int port = jsonObject.get("port").getAsInt();
                    byteCloudMaster.getProxy().getServers().put(serverId, byteCloudMaster.getProxy()
                            .constructServerInfo(serverId, new InetSocketAddress("localhost", port), "ByteCloud Minecraft-Server", false));
                    break;
                case OUT_SEND_MESSAGE:
                    String player = jsonObject.get("player").getAsString();
                    String message = jsonObject.get("message").getAsString().replace("#&C#", "§");
                    ProxiedPlayer pp = byteCloudMaster.getProxy().getPlayer(player);
                    if(pp != null)
                        pp.sendMessage(byteCloudMaster.prefix+"§r"+message);
                    break;
                case OUT_UNREGISTER_SERVER:
                    serverId = jsonObject.get("serverId").getAsString();
                    byteCloudMaster.getProxy().getServers().remove(serverId);
                    break;
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
