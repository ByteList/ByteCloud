package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.server.group.ServerGroup;
import de.bytelist.bytecloud.server.group.ServerGroupObject;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 27.01.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerHandler {

    @Getter
    private HashMap<String, ServerGroup> serverGroups;
    @Getter
    private ArrayList<PermServer> permanentServers;

    private HashMap<String, Server> servers;

    private boolean areServersRunning;

    public ServerHandler() {
        this.serverGroups = new HashMap<>();
        this.permanentServers = new ArrayList<>();
        this.servers = new HashMap<>();
        this.areServersRunning = false;

        final File servGroups = new File(EnumFile.TEMPLATES.getPath());
        final File permServs = new File(EnumFile.SERVERS_PERMANENT.getPath());

        if(servGroups.list().length > 0) {
            for(String templates : servGroups.list()) {
                final ServerGroupObject serverGroupObject = new ServerGroupObject(templates);
                ServerGroup serverGroup = new ServerGroup(templates, serverGroupObject);
                this.serverGroups.put(templates, serverGroup);
            }
        }

        if(permServs.list().length > 0) {
            for(String servs : permServs.list()) {
                final PermServerObject permServerObject = new PermServerObject(servs);
                if(permServerObject.get("autoStart").getAsBoolean()) {
                    PermServer permServer = new PermServer(servs, permServerObject.get("port").getAsInt(),
                            permServerObject.get("ram").getAsInt(),
                            permServerObject.get("player").getAsInt(),
                            permServerObject.get("spectator").getAsInt());
                    this.permanentServers.add(permServer);
                    this.servers.put(servs, permServer);
                }
            }
        }
        try {
            FileUtils.cleanDirectory(new File(EnumFile.SERVERS_RUNNING.getPath()));
        } catch (IOException ignored) { }
    }

    public void start() {
        System.out.println("Starting servers...");
        for(PermServer permServer : permanentServers) {
            permServer.startServer("_cloud");
        }
        if(serverGroups.containsKey("LOBBY")) {
            ByteCloud.getInstance().getLogger().info("** Startup changed. Run lobbies first.");
            ServerGroup lobbyGroup = serverGroups.get("LOBBY");
            lobbyGroup.onStart();
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(ServerGroup serverGroup : serverGroups.values()) {
            serverGroup.onStart();
            serverGroup.start();
        }
    }

    public void stop() {
        System.out.println("Stopping servers...");
        Thread thread = new Thread("Server Stop Thread") {
            @Override
            public void run() {
                ArrayList<Server> servers1 = new ArrayList<>();
                servers1.addAll(getServers());
                for(Server server : servers1) {
                    server.stopServer("_cloud");
                }
                while (true) {
                    if (servers.size() == 0) {
                        areServersRunning = false;
                        break;
                    }
                }
            }
        };
        thread.start();

        while (true) {
            if(areServersRunning && thread.isAlive()) break;
        }
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Servers stopped!");
    }

    public Collection<Server> getServers() {
        return Collections.unmodifiableCollection(servers.values());
    }

    public Server getServer(String serverId) {
        for(Server server : servers.values()) {
            if(serverId.equals(server.getServerId())) {
                return server;
            }
        }
        return null;
    }

    public boolean existsServer(String serverId) {
        return servers.containsKey(serverId);
    }

    public boolean existsPermanentServer(String serverName) {
        for(String servs : new File(EnumFile.SERVERS_PERMANENT.getPath()).list()) {
            if(servs.equals(serverName)) {
                return true;
            }
        }
        return false;
    }

    public void registerServer(Server server) {
        this.servers.put(server.getServerId(), server);
    }

    public void unregisterServer(String serverId) {
        Server server = this.servers.remove(serverId);
        if(server instanceof TempServer) {
            ((TempServer) server).getServerGroup().removeServer((TempServer) server);
        } else if(server instanceof PermServer) {
            this.permanentServers.remove(server);
        }
    }

    void setAreServersRunning() {
        if(!areServersRunning)
            areServersRunning = true;
    }

    public String getRandomLobbyId() {
        List<String> lobbyServer = new ArrayList<>();
        lobbyServer.addAll(ByteCloud.getInstance().getDatabaseServer().getServer("LOBBY"));

        int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

        return lobbyServer.get(i);
    }

    public int getDatabasePlayers(String serverId) {
        return ByteCloud.getInstance().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt();
    }

    public int getDatabaseSpectators(String serverId) {
        return ByteCloud.getInstance().getDatabaseServer().getDatabaseElement(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt();
    }

    public int getDatabaseOnlineCount(String serverId) {
        return getDatabasePlayers(serverId) + getDatabaseSpectators(serverId);
    }
}