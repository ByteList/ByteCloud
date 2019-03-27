package de.bytelist.bytecloud.core.cloud;

import de.bytelist.bytecloud.CloudAPIHandler;
import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerChangedStatePacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerSetMotdPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerKickPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerMessagePacket;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.bytelist.bytecloud.core.ByteCloudCore;
import de.bytelist.bytecloud.core.event.ByteCloudPlayerConnectToServerEvent;
import de.bytelist.bytecloud.core.event.ByteCloudServerUpdateEvent;
import de.bytelist.bytecloud.core.event.ByteCloudServerUpdateStateEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 20.12.2016.
 */
public class CloudHandler extends CloudAPIHandler {

    private final ByteCloudCore byteCloudCore = ByteCloudCore.getInstance();

    @Getter
    private String serverId, serverGroup;

    @Getter @Setter
    private String cloudVersion, cloudStarted;
    @Getter @Setter
    private boolean cloudRunning;

    public CloudHandler() {
        this.serverId = System.getProperty("de.bytelist.bytecloud.servername", Bukkit.getServerName());
        this.serverGroup = System.getProperty("de.bytelist.bytecloud.servergroup", "null");
    }

    @Override
    public void kickCloudPlayer(CloudPlayerKickPacket cloudPlayerKickPacket) {
        Player player = Bukkit.getPlayer(cloudPlayerKickPacket.getUuid());

        if(player != null)
            Bukkit.getScheduler().runTaskLater(ByteCloudCore.getInstance(), ()-> player.kickPlayer(cloudPlayerKickPacket.getReason()), 2);
    }

    @Override
    public void sendMessage(CloudPlayerMessagePacket cloudPlayerMessagePacket) {
        Player player = Bukkit.getPlayer(cloudPlayerMessagePacket.getUuid());

        if(player != null)
            player.sendMessage(cloudPlayerMessagePacket.getMessage());
    }

    @Override
    public void setServerState(CloudServerChangedStatePacket cloudServerChangedStatePacket) {
        super.setServerState(cloudServerChangedStatePacket);

        CloudServer cloudServer = getCloudServer(cloudServerChangedStatePacket.getServerId());

        if(cloudServer != null)
            Bukkit.getPluginManager().callEvent(new ByteCloudServerUpdateStateEvent(cloudServer.getServerId(),
                cloudServer.getServerGroup().getGroupName(),
                cloudServerChangedStatePacket.getOld(), cloudServerChangedStatePacket.getState()));
    }

    public Integer getSocketPort() {
        return byteCloudCore.getCloudConfig().getInt("socket-port");
    }
}
