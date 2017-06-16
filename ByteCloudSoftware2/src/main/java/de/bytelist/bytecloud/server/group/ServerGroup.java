package de.bytelist.bytecloud.server.group;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.server.TempServer;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ByteList on 18.02.2017.
 */
public class ServerGroup {

    private static final char[] POOL = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Random rnd = new Random();

    private static char randomChar() {
        return POOL[rnd.nextInt(POOL.length)];
    }

    private static String randomKey(int length) {
        StringBuilder kb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            kb.append(randomChar());
        }
        return kb.toString();
    }

//    private ServerGroup serverGroup;

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    @Getter
    private String name, prefix/*, motd*/;

    @Getter
    private int amount, max, startPort, player, spectator, ram;

    @Getter
    private File directory;

//    @Getter
//    private Server.ServerType serverType;

    @Getter
    private List<TempServer> servers = new ArrayList<>();
    @Getter
    private List<Integer> usedIds = new ArrayList<>();
    @Getter
    private List<Integer> usedPorts = new ArrayList<>();


    public ServerGroup(String group, ServerGroupObject serverGroupObject/*, Server.ServerType serverType*/) {
//        this.serverGroup = this;

        this.name = group.toUpperCase();
        this.prefix = serverGroupObject.get("prefix").getAsString();
//        this.motd = serverGroupObject.get("motd").getAsString();

        this.amount = serverGroupObject.get("amount").getAsInt();
        this.max = serverGroupObject.get("max").getAsInt();
        this.startPort = serverGroupObject.get("port").getAsInt();
        this.player = serverGroupObject.get("player").getAsInt();
        this.spectator = serverGroupObject.get("spectator").getAsInt();

//        this.serverType = serverType;

        this.ram = serverGroupObject.get("ram").getAsInt();

        this.directory = new File(EnumFile.TEMPLATES.getPath(), group);
    }

    public void onStart() {
        int i = 0;
        while (i < amount) {
            i++;
            startNewServer("_cloud");
        }
    }

    private String generateServerId() {
        int min = 1;

        for (Integer i : usedIds) if (i.equals(min)) min = min + 1;
        usedIds.add(min);
        return prefix + "-" + min + "-" + randomKey(5);
    }

    private Integer getNextServerPort() {
        int min = startPort;

        for (Integer i : usedPorts) if (i.equals(min)) min = min + 10;

        this.usedPorts.add(min);
        return min;
    }

    public void startNewServer(String sender) {
        if(byteCloud.isRunning) {
            final String serverId = generateServerId();
            final int port = getNextServerPort();
            TempServer tempServer = new TempServer(serverId, port, this);

            this.servers.add(tempServer);

            tempServer.startServer(sender, this.ram, this.player, this.spectator);
        }
    }

    public void removeServer(TempServer tempServer) {
        this.usedIds.remove(Integer.valueOf(tempServer.getServerId().split("-")[1]));
        this.usedPorts.remove(Integer.valueOf(tempServer.getPort()));
        this.servers.remove(tempServer);
    }

}
