package de.bytelist.bytecloud.network.server;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.client.JsonClientListener;
import de.bytelist.bytecloud.core.ByteCloudCore;
import de.bytelist.bytecloud.core.cloud.CloudAPI;
import de.bytelist.bytecloud.core.cloud.CloudHandler;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutChangeServerState;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutCloudInfo;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutExecuteCommand;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutKickAllPlayers;
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
            String packet = jsonObject.get("packet").getAsString();
            if(packet.equals(PacketOutKickAllPlayers.class.getSimpleName())) {
                String reason = jsonObject.get("reason").getAsString();
                new Thread("Async Player Kick Thread") {
                    @Override
                    public void run() {
                        for(Player player : Bukkit.getOnlinePlayers()) {
                            player.kickPlayer("§7"+reason.replace("#&C#", "§"));
                        }
                    }
                }.start();
            }
            if(packet.equals(PacketOutCloudInfo.class.getSimpleName())) {
                CloudHandler cloudHandler = byteCloudCore.getCloudHandler();
                cloudHandler.setCloudVersion(jsonObject.get("cloudVersion").getAsString());
                cloudHandler.setCloudStarted(jsonObject.get("cloudStarted").getAsString());
                cloudHandler.setCloudRunning(jsonObject.get("cloudRunning").getAsBoolean());
            }
            if(packet.equals(PacketOutChangeServerState.class.getSimpleName())) {
                String serverId = jsonObject.get("serverId").getAsString(),
                        serverGroup = jsonObject.get("serverGroup").getAsString();
                CloudAPI.ServerState oldState = CloudAPI.ServerState.valueOf(jsonObject.get("oldState").getAsString()),
                        newState = CloudAPI.ServerState.valueOf(jsonObject.get("newState").getAsString());
                byteCloudCore.getCloudHandler().callCloudServerUpdateStateEvent(serverId, serverGroup, oldState, newState);
            }

            if(packet.equals(PacketOutExecuteCommand.class.getSimpleName())) {
                String cmd = jsonObject.get("command").getAsString();
                byteCloudCore.getLogger().info("Execute cmd from cloud: "+cmd);
                byteCloudCore.getServer().dispatchCommand(byteCloudCore.getServer().getConsoleSender(), cmd);
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
