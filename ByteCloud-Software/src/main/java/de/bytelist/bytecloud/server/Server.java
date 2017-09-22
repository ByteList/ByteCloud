package de.bytelist.bytecloud.server;

import lombok.Getter;
import lombok.Setter;

import java.io.File;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public abstract class Server implements Executable {

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

    public enum ServerState {
        STARTING,
        LOBBY,
        FULL,
        INGAME,
        RESTART,
        STOPPED
    }
}
