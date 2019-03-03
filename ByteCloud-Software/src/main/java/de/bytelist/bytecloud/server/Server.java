package de.bytelist.bytecloud.server;

import com.github.steveice10.packetlib.Session;
import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.Executable;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerChangedStatePacket;
import de.bytelist.bytecloud.server.screen.IScreen;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class Server implements IScreen, Executable {

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
    protected boolean serverPermanent;
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
    }

    public boolean isRunning() {
        return this.process != null && this.process.isAlive();
    }

    public void setServerState(ServerState serverState) {
        ByteCloud.getInstance().sendGlobalPacket(new CloudServerChangedStatePacket(this.serverId, this.serverState, serverState));
        this.serverState = serverState;
    }

    @Override
    public String toString() {
        return "Server[id="+serverId+",port="+port+",ram="+ramM+",running="+isRunning()+"]";
    }
}
