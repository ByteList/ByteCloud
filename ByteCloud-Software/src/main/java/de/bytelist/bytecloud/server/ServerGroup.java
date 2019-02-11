package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ByteList on 18.02.2017.
 */
public class ServerGroup {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

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

    @Getter
    private String groupName, prefix;

    @Getter
    private int amount, max, startPort, player, spectator, ram;

    @Getter
    private File directory;

    private boolean started;

    @Getter
    private ArrayList<String> servers = new ArrayList<>();
    @Getter
    private ArrayList<Integer> usedIds = new ArrayList<>();
    @Getter
    private ArrayList<Integer> usedPorts = new ArrayList<>();

    ServerGroup(String group, ServerDocument serverDocument) {
        this.groupName = serverDocument.get("name").getAsString();
        this.prefix = serverDocument.get("prefix").getAsString();
        this.amount = serverDocument.get("amount").getAsInt();
        this.max = serverDocument.get("max").getAsInt();
        this.startPort = serverDocument.get("port").getAsInt();
        this.player = serverDocument.get("player").getAsInt();
        this.spectator = serverDocument.get("spectator").getAsInt();
        this.ram = serverDocument.get("ram").getAsInt();

        this.directory = new File(EnumFile.TEMPLATES.getPath(), group.toUpperCase());
    }

    void start() {
        if(!started) {
            this.started = true;
            int i = 0;
            while (i < amount) {
                i++;
                startNewServer("_cloud");
            }
        }
    }

    private String generateServerId() {
        int min = 1;

        for (Integer i : usedIds) if (i.equals(min)) min = min + 1;
        usedIds.add(min);
        return prefix + "-" + min + "-" + randomKey(15);
    }

    private Integer getNextServerPort() {
        int min = startPort;

        for (Integer i : usedPorts) if (i.equals(min)) min = min + 10;

        this.usedPorts.add(min);
        return min;
    }

    public void startNewServer(String sender) {
        if(byteCloud.isRunning) {
            if(byteCloud.getUsedMemory()+ram < byteCloud.getMaxMemory() && this.servers.size() < max) {
                String serverId = generateServerId();
                int port = getNextServerPort();
                TempServer tempServer = new TempServer(serverId, port, this.ram, this.player, this.spectator, this);

                this.servers.add(tempServer.getServerId());

                tempServer.startServer(sender);
            } else {
                if(!sender.equals("_cloud")) {
//                    PacketOutSendMessage packetOutSendMessage = new PacketOutSendMessage(sender, "§cToo much servers are currently online!");
//                    byteCloud.getCloudServer().sendPacket("Bungee-1", packetOutSendMessage);
                } else {
                    System.out.println("ServerGroup "+this.groupName+": Too much servers are currently online!");
                }
            }
        }
    }

    void removeServer(TempServer tempServer) {
        this.usedIds.remove(Integer.valueOf(tempServer.getServerId().split("-")[1]));
        this.usedPorts.remove(Integer.valueOf(tempServer.getPort()));
        this.servers.remove(tempServer.getServerId());
    }

    public void checkAndStartNewServer() {
        if(byteCloud.isRunning) {
            boolean b = false;
            for (String server : servers) {
                Server.ServerState serverState = byteCloud.getServerHandler().getServer(server).getServerState();
                if (serverState == Server.ServerState.STARTING || serverState == Server.ServerState.LOBBY) {
                    b = true;
                    break;
                }
            }

            if (!b) {
                startNewServer("_cloud");
            }
        }
    }

}
