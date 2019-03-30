package de.bytelist.bytecloud;

import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.packet.cloud.*;
import de.bytelist.bytecloud.common.packet.cloud.player.*;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.bytelist.bytecloud.common.server.CloudServerGroup;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
    private final HashMap<String,CloudServer> cloudServers = new HashMap<>();
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

        System.out.println("CloudAPIHandler.addCloudServerGroup: "+cloudServerGroupInfoPacket.getGroupName());
    }

    public void addCloudServer(CloudServerStartedPacket cloudServerStartedPacket) {
        CloudServer cloudServer = new CloudServer(cloudServerStartedPacket);

        if(!cloudServer.isServerPermanent()) {
            cloudServer.getServerGroup().addServer(cloudServer);
        } else {
            this.permanentCloudServers.add(cloudServer);
        }

        this.cloudServers.put(cloudServer.getServerId(), cloudServer);

        System.out.println("CloudAPIHandler.addCloudServer: "+cloudServerStartedPacket.getServerId());
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

            this.cloudServers.remove(cloudServer.getServerId());
        }

        System.out.println("CloudAPIHandler.removeCloudServer: "+cloudServerStoppedPacket.getServerId());
    }

    public void addCloudPlayer(CloudPlayerConnectPacket cloudPlayerConnectPacket) {
        CloudPlayer cloudPlayer = new CloudPlayer(cloudPlayerConnectPacket.getUuid(), cloudPlayerConnectPacket.getName());
        this.cloudPlayers.add(cloudPlayer);

        System.out.println("CloudAPIHandler.addCloudPlayer: "+cloudPlayerConnectPacket.getUuid());
    }

    public void removeCloudPlayer(CloudPlayerDisconnectPacket cloudPlayerDisconnectPacket) {
        CloudPlayer cloudPlayer = getCloudPlayer(cloudPlayerDisconnectPacket.getUuid());

        if(cloudPlayer != null)
            cloudPlayer.getCurrentServer().removePlayer(cloudPlayer);

        System.out.println("CloudAPIHandler.removeCloudPlayer: "+cloudPlayerDisconnectPacket.getUuid());
    }

    public void updateCloudPlayerCurrentServer(CloudPlayerServerSwitchPacket cloudPlayerServerSwitchPacket) {
        CloudPlayer cloudPlayer = getCloudPlayer(cloudPlayerServerSwitchPacket.getUuid());
        CloudServer cloudServer = getCloudServer(cloudPlayerServerSwitchPacket.getServerId());

        if(cloudPlayer != null && cloudServer != null)
            cloudPlayer.setCurrentServer(cloudServer);

        System.out.println("CloudAPIHandler.updateCloudPlayerCurrentServer: "+cloudPlayerServerSwitchPacket.getUuid()+" : "+cloudPlayerServerSwitchPacket.getServerId());
    }

    public abstract void kickCloudPlayer(CloudPlayerKickPacket cloudPlayerKickPacket);

    public abstract void sendMessage(CloudPlayerMessagePacket cloudPlayerMessagePacket);

    public void setMotd(CloudServerSetMotdPacket cloudServerSetMotdPacket) {
        CloudServer cloudServer = this.getCloudServer(cloudServerSetMotdPacket.getServerId());

        if(cloudServer != null) {
            cloudServer.setMotd(cloudServerSetMotdPacket.getMotd());
        }
    }

    public void setServerState(CloudServerChangedStatePacket cloudServerChangedStatePacket) {
        CloudServer cloudServer = this.getCloudServer(cloudServerChangedStatePacket.getServerId());

        if(cloudServer != null) {
            cloudServer.setServerState(cloudServerChangedStatePacket.getState());
        }
    }

    public CloudServer getCloudServer(String serverId) {
        for (String id : this.cloudServers.keySet()) {
            if(id.equals(serverId)) {
                return this.cloudServers.get(id);
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

    public String getRandomLobbyId() {
        List<CloudServer> lobbyServer = new ArrayList<>(getCloudServerGroups().get("Lobby").getServers());
        int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

        return lobbyServer.get(i).getServerId();
    }

    public String getRandomLobbyId(String excludedLobbyId) {
        List<CloudServer> lobbyServer = new ArrayList<>(getCloudServerGroups().get("Lobby").getServers());
        for(CloudServer lb : lobbyServer)
            if(lb.getServerId().equals(excludedLobbyId)) lobbyServer.remove(lb);

        int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

        return lobbyServer.get(i).getServerId();
    }

}
