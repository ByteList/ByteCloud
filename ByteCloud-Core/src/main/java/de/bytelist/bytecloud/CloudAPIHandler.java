package de.bytelist.bytecloud;

import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerGroupInfoPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStartedPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStoppedPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerConnectPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerDisconnectPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerKickPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerServerSwitchPacket;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.bytelist.bytecloud.common.server.CloudServerGroup;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by ByteList on 03.03.2019.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class CloudAPIHandler {

    @Getter
    private static CloudAPIHandler instance;

    @Getter
    private final HashMap<String, CloudServerGroup> cloudServerGroups = new HashMap<>();
    @Getter
    private final List<CloudServer> cloudServers = new ArrayList<>();
    @Getter
    private final List<CloudServer> permanentCloudServers = new ArrayList<>();
    @Getter
    private final List<CloudPlayer> cloudPlayers = new ArrayList<>();

    protected CloudAPIHandler() {
        instance = this;
    }

    public void addCloudServerGroup(CloudServerGroupInfoPacket cloudServerGroupInfoPacket) {
        CloudServerGroup cloudServerGroup = new CloudServerGroup(cloudServerGroupInfoPacket);
        this.cloudServerGroups.put(cloudServerGroup.getGroupName(), cloudServerGroup);
    }

    public void addCloudServer(CloudServerStartedPacket cloudServerStartedPacket) {
        CloudServer cloudServer = new CloudServer(cloudServerStartedPacket);

        if(!cloudServer.isServerPermanent()) {
            cloudServer.getServerGroup().addServer(cloudServer);
        } else {
            this.permanentCloudServers.add(cloudServer);
        }
    }

    public void removeCloudServer(CloudServerStoppedPacket cloudServerStoppedPacket) {
        CloudServer cloudServer = getCloudServer(cloudServerStoppedPacket.getServerId());

        if(cloudServer != null) {
            cloudServer.syncStop(cloudServerStoppedPacket.getReason());

            if(cloudServer.isServerPermanent()) {
                this.permanentCloudServers.remove(cloudServer);
            } else {
                cloudServer.getServerGroup().removeServer(cloudServer);
            }

            this.cloudServers.remove(cloudServer);
        }
    }

    public void addCloudPlayer(CloudPlayerConnectPacket cloudPlayerConnectPacket) {
        CloudPlayer cloudPlayer = new CloudPlayer(cloudPlayerConnectPacket.getUuid(), cloudPlayerConnectPacket.getName());
        this.cloudPlayers.add(cloudPlayer);
    }

    public void removeCloudPlayer(CloudPlayerDisconnectPacket cloudPlayerDisconnectPacket) {
        CloudPlayer cloudPlayer = getCloudPlayer(cloudPlayerDisconnectPacket.getUuid());

        if(cloudPlayer != null)
            cloudPlayer.getCurrentServer().removePlayer(cloudPlayer);
    }

    public void updateCloudPlayerCurrentServer(CloudPlayerServerSwitchPacket cloudPlayerServerSwitchPacket) {
        CloudPlayer cloudPlayer = getCloudPlayer(cloudPlayerServerSwitchPacket.getUuid());
        CloudServer cloudServer = getCloudServer(cloudPlayerServerSwitchPacket.getServerId());

        if(cloudPlayer != null && cloudServer != null)
            cloudPlayer.setCurrentServer(cloudServer);
    }

    public abstract void kickCloudPlayer(CloudPlayerKickPacket cloudPlayerKickPacket);

    public CloudServer getCloudServer(String serverId) {
        for (CloudServer cloudServer : this.cloudServers) {
            if(cloudServer.getServerId().equals(serverId)) {
                return cloudServer;
            }
        }
        return null;
    }

    public CloudPlayer getCloudPlayer(String name) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if(cloudPlayer.getName().equals(name)) {
                return cloudPlayer;
            }
        }
        return null;
    }

    public CloudPlayer getCloudPlayer(UUID uuid) {
        for (CloudPlayer cloudPlayer : this.cloudPlayers) {
            if(cloudPlayer.getUuid().toString().equals(uuid.toString())) {
                return cloudPlayer;
            }
        }
        return null;
    }

}
