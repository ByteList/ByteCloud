package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.server.group.ServerGroup;
import de.bytelist.bytecloud.server.group.ServerGroupObject;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 27.01.2017.
 */
public class ServerHandler {

    @Getter
    private ArrayList<ServerGroup> serverGroups;
    @Getter
    private ArrayList<PermServer> permanentServers;

    private boolean areServersRunning;

    public ServerHandler() {
        this.serverGroups = new ArrayList<>();
        this.permanentServers = new ArrayList<>();
        this.areServersRunning = false;

        final File servGroups = new File(EnumFile.TEMPLATES.getPath());
        final File permServs = new File(EnumFile.SERVERS_PERMANENT.getPath());

        if(servGroups.list().length > 0) {
            for(String templates : servGroups.list()) {
                final ServerGroupObject serverGroupObject = new ServerGroupObject(templates);
                ServerGroup serverGroup = new ServerGroup(templates, serverGroupObject);
                this.serverGroups.add(serverGroup);
            }
        }

        if(permServs.list().length > 0) {
            for(String servs : permServs.list()) {
                final PermServerObject permServerObject = new PermServerObject(servs);
                if(permServerObject.get("autoStart").getAsBoolean()) {
                    PermServer permServer = new PermServer(servs, permServerObject);
                    this.permanentServers.add(permServer);
                }
            }
        }
    }

    public void start() {
        System.out.println("Starting servers...");
        for(PermServer permServer : permanentServers) {
            PermServerObject permServerObject = permServer.getPermServerObject();
            permServer.startServer("_cloud",
                    permServerObject.get("ram").getAsInt(),
                    permServerObject.get("player").getAsInt(),
                    permServerObject.get("spectator").getAsInt());
        }
        for(ServerGroup serverGroup : serverGroups) {
            serverGroup.onStart();
        }
    }

    public void stop() {
        System.out.println("Stopping servers...");
        Thread thread = new Thread("Server Stop Thread") {
            @Override
            public void run() {
                for(Server server : getServers()) {
                    if(server instanceof PermServer) {
                        ((PermServer) server).stopServer("_cloud", Server.StopType.KICK);
                    } else if(server instanceof TempServer)
                        ((TempServer) server).stopServer("_cloud", Server.StopType.KICK);
                    else server.stopServer(Server.StopType.KICK);
                }
                while (true) {
                    if (getServers().size() == 0) break;
                }
                areServersRunning = false;
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

    public List<Server> getServers() {
        ArrayList<Server> servers = new ArrayList<>();
        servers.addAll(permanentServers);
        for(ServerGroup serverGroup : serverGroups) {
            servers.addAll(serverGroup.getServers());
        }
        return Collections.unmodifiableList(servers);
    }

    public Server getServer(String serverId) {
        for(Server server : getServers()) {
            if(serverId.equals(server.getServerId())) {
                return server;
            }
        }
        return null;
    }

    public boolean existsServer(String serverId) {
        return getServer(serverId) != null;
    }

    public boolean existsPermanentServer(String serverName) {
        for(String servs : new File(EnumFile.SERVERS_PERMANENT.getPath()).list()) {
            if(servs.equals(serverName)) {
                return true;
            }
        }
        return false;
    }

    void removePermanentServer(PermServer permServer) {
        this.permanentServers.remove(permServer);
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
