package de.bytelist.bytecloud.network.server;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.client.JsonClientListener;
import de.bytelist.bytecloud.core.ByteCloudCore;
import de.bytelist.bytecloud.core.cloud.CloudHandler;
import de.bytelist.bytecloud.core.event.CloudEvent;
import de.bytelist.bytecloud.network.PacketName;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 26.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerClientListener extends JsonClientListener {

    private final ByteCloudCore byteCloudCore = ByteCloudCore.getInstance();

    @Override
    public void jsonReceived(JsonObject jsonObject) {
        if(jsonObject.has("packet")) {
            PacketName packet = PacketName.getPacketName(jsonObject.get("packet").getAsString());
            String serverId, serverGroup, sender, reason, players;

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
                case OUT_CLOUD_INFO:
                    CloudHandler cloudHandler = byteCloudCore.getCloudHandler();
                    cloudHandler.setCloudVersion(jsonObject.get("cloudVersion").getAsString());
                    cloudHandler.setCloudStarted(jsonObject.get("cloudStarted").getAsString());
                    cloudHandler.setCloudRunning(jsonObject.get("cloudRunning").getAsBoolean());
                    break;
                case OUT_EXECUTE_COMMAND:
                    String cmd = jsonObject.get("command").getAsString();
                    byteCloudCore.getLogger().info("Execute cmd from cloud: "+cmd);
                    byteCloudCore.getServer().dispatchCommand(byteCloudCore.getServer().getConsoleSender(), cmd);
                    break;
                case OUT_KICK_ALL_PLAYERS:
                    reason = jsonObject.get("reason").getAsString();
                    new Thread("Async Player Kick Thread") {
                        @Override
                        public void run() {
                            for(Player player : Bukkit.getOnlinePlayers()) {
                                player.kickPlayer("§7"+reason.replace("#&C#", "§"));
                            }
                        }
                    }.start();
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
                case OUT_CALL_CLOUD_EVENT:
                    String[] event = jsonObject.get("event").getAsString().split(":");
                    int id;

                    try {
                        id = Integer.valueOf(event[0]);
                    } catch (NumberFormatException ex) {
                        byteCloudCore.getLogger().warning(PacketName.OUT_CALL_CLOUD_EVENT.getPacketName()+" throws: "+ex.getMessage());
                        break;
                    }

                    CloudEvent cloudEvent = CloudEvent.getCloudEventFromId(id);

                    switch (cloudEvent) {
                        case UNKNOWN:
                            break;
                        case SERVER_UPDATE:
                            byteCloudCore.getCloudHandler().callCloudServerUpdateEvent(event[1], event[2]);
                            break;
                        case SERVER_UPDATE_STATE:
                            byteCloudCore.getCloudHandler().callCloudServerUpdateStateEvent(event[1], event[2], event[3], event[4]);
                            break;
                        case PLAYER_CONNECT:
                            byteCloudCore.getCloudHandler().callCloudPlayerConnectToServerEvent(event[1], event[2], event[3], event[4], event[5]);
                        break;
                    }
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
