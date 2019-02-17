package de.bytelist.bytecloud.server;

import com.github.steveice10.packetlib.Session;
import de.bytelist.bytecloud.common.Executable;
import de.bytelist.bytecloud.common.ServerState;
import de.bytelist.bytecloud.server.screen.IScreen;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class Server implements IScreen, Executable {

    @Getter @Setter
    protected Session session;
    @Getter
    protected String serverId;
    @Getter
    protected int port;
    @Getter
    protected int ramM, maxPlayer, maxSpectator;
    @Getter @Setter
    protected ServerState serverState;
    @Getter
    protected File directory;
    @Getter
    protected volatile Process process;

    String starter;
    String stopper;

    Server(String serverId, int port, int ramM, int maxPlayer, int maxSpectator, ServerState serverState, String directory) {
        this.serverId = serverId;
        this.port = port;
        this.ramM = ramM;
        this.maxPlayer = maxPlayer;
        this.maxSpectator = maxSpectator;
        this.serverState = serverState;
        this.directory = new File(directory, serverId);
    }

    public boolean isRunning() {
        return this.process != null && this.process.isAlive();
    }

    @Override
    public String toString() {
        return "Server[id="+serverId+",port="+port+",ram="+ramM+",running="+isRunning()+"]";
    }
}
