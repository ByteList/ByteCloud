package de.bytelist.bytecloud.server;

import com.github.steveice10.packetlib.Session;
import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.IServer;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerChangedStatePacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerGroupInfoPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerSetMotdPacket;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStartedPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerConnectPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerMessagePacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerServerSwitchPacket;
import de.bytelist.bytecloud.server.screen.IScreen;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class Server implements IScreen, IServer {

    protected final ByteCloud byteCloud = ByteCloud.getInstance();

    @Getter @Setter
    protected Session session;
    @Getter
    protected String serverId, motd;
    @Getter
    protected int port;
    @Getter
    protected int ramM, slots, currentPlayers;
    @Getter
    protected ServerState serverState;
    @Getter
    protected File directory;
    @Getter
    protected volatile Process process;
    @Getter
    protected boolean serverPermanent, started;
    @Getter
    protected List<CloudPlayer> players = new ArrayList<>();

    String starter;
    String stopper;

    Server(String serverId, int port, int ramM, int slots, ServerState serverState, String directory) {
        this.serverId = serverId;
        this.port = port;
        this.ramM = ramM;
        this.slots = slots;
        this.serverState = serverState;
        this.directory = new File(directory, serverId);
        this.serverPermanent = false;
        this.motd = "Starting server...";
    }

    public boolean isRunning() {
        return this.process != null && this.process.isAlive();
    }

    @Override
    public void setServerState(ServerState serverState) {
        ByteCloud.getInstance().sendGlobalPacket(new CloudServerChangedStatePacket(this.serverId, this.serverState, serverState));
        this.serverState = serverState;
    }

    public void onStart() {
        for (ServerGroup serverGroup : byteCloud.getServerHandler().getServerGroups().values()) {
            this.session.send(new CloudServerGroupInfoPacket(serverGroup.getGroupName(), serverGroup.getPrefix(),
                    serverGroup.getMaxServers(), serverGroup.getSlotsPerServer()));
        }
        for (Server server : byteCloud.getServerHandler().getServers()) {
            if(server.isStarted()) {
                this.session.send(new CloudServerStartedPacket(server.getServerId(), server.getPort(),
                        server.isServerPermanent() ? "{null}" : ((TempServer)server).getServerGroup().getGroupName(),
                        server.isServerPermanent(), server.getSlots(), server.getMotd()));

                for (CloudPlayer cloudPlayer : server.getPlayers()) {
                    System.out.println("Server.onStart: for cloudPlayer: "+cloudPlayer.getUuid());
                    this.session.send(new CloudPlayerConnectPacket(cloudPlayer.getUuid(), cloudPlayer.getName()));
                    this.session.send(new CloudPlayerServerSwitchPacket(cloudPlayer.getUuid(), cloudPlayer.getCurrentServer().getServerId()));
                }
            }
        }
    }

    @Override
    public void runCommand(String command) {
        String x = command + "\n";
        if (this.process != null) {
            try {
                this.process.getOutputStream().write(x.getBytes());
                this.process.getOutputStream().flush();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }
    }

    void manageProcessOnStop() {
        if(this.process.isAlive()) {
            List<CloudPlayer> cloudPlayers = new ArrayList<>(this.players);

            if (byteCloud.isRunning) {
                String lobbyId = byteCloud.getServerHandler().getRandomLobbyId(serverId);

                if (!byteCloud.getServerIdOnConnect().equals(this.serverId)) {
                    cloudPlayers.forEach(cloudPlayer -> {
                        byteCloud.sendGlobalPacket(new CloudPlayerServerSwitchPacket(cloudPlayer.getUuid(), lobbyId));
                        byteCloud.getBungee().getSession().send(new CloudPlayerMessagePacket(cloudPlayer.getUuid(),
                                "§6Verbinde zur Lobby..."));
                    });
                } else {
                    cloudPlayers.forEach(cloudPlayer -> {
                        byteCloud.kickPlayer(cloudPlayer.getUuid(),
                                "§7Server stopped.\n§cDu konntest nicht zur Lobby verbunden werden!");
                    });
                }
            }
            while (true) {
                if (this.currentPlayers < 1)
                    break;
                else {
                    try {
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                this.process.getOutputStream().write("stop\n".getBytes());
                this.process.getOutputStream().flush();
                Thread.sleep(1500L);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        byteCloud.getScreenManager().checkAndRemove(this);
        this.process.destroy();
    }


    @Override
    public void setMotd(String motd) {
        this.motd = motd;
        byteCloud.sendGlobalPacket(new CloudServerSetMotdPacket(this.serverId, motd));
    }

    @Override
    public void addPlayer(CloudPlayer cloudPlayer) {
        this.players.add(cloudPlayer);
        byteCloud.sendGlobalPacket(new CloudPlayerServerSwitchPacket(cloudPlayer.getUuid(), this.serverId));
    }

    @Override
    public String toString() {
        return "Server[id="+serverId+",port="+port+",ram="+ramM+",running="+isRunning()+"]";
    }
}
