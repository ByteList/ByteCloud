package de.bytelist.bytecloud.bungee.cloud;

import de.bytelist.bytecloud.CloudAPIHandler;
import de.bytelist.bytecloud.ServerIdResolver;
import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStartedPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerKickPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerMessagePacket;
import de.bytelist.bytecloud.common.server.CloudServer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by ByteList on 28.01.2017.
 */
public class CloudHandler extends CloudAPIHandler {

    private final ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();

    @Getter
    private final String bungeeId;

    @Getter @Setter
    private String cloudVersion, cloudStarted;
    @Getter @Setter
    private boolean cloudRunning;

    public CloudHandler() {
        this.bungeeId = System.getProperty("de.bytelist.bytecloud.servername", "Bungee-0");
    }

    @Override
    public void kickCloudPlayer(CloudPlayerKickPacket cloudPlayerKickPacket) {
        ProxiedPlayer player = ByteCloudMaster.getInstance().getProxy().getPlayer(cloudPlayerKickPacket.getUuid());
        player.disconnect(cloudPlayerKickPacket.getReason());
    }

    @Override
    public void addCloudServer(CloudServerStartedPacket cloudServerStartedPacket) {
        super.addCloudServer(cloudServerStartedPacket);
        byteCloudMaster.getProxy().getServers().put(cloudServerStartedPacket.getServerId(), byteCloudMaster.getProxy()
                .constructServerInfo(cloudServerStartedPacket.getServerId(),
                        new InetSocketAddress("localhost", cloudServerStartedPacket.getPort()),
                        "ByteCloud Minecraft-Server", false));

    }

    @Override
    public void sendMessage(CloudPlayerMessagePacket cloudPlayerMessagePacket) {
        ProxiedPlayer player = ByteCloudMaster.getInstance().getProxy().getPlayer(cloudPlayerMessagePacket.getUuid());
        player.sendMessage(cloudPlayerMessagePacket.getMessage());
    }

    public Integer getSocketPort() {
        return byteCloudMaster.getCloudConfig().getInt("socket-port");
    }

    public String getCloudAddress() {
        return "127.0.0.1";
    }

    public String getRandomLobbyId() {
        List<CloudServer> lobbyServer = new ArrayList<>(getCloudServerGroups().get("LOBBY").getServers());
        int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

        return lobbyServer.get(i).getServerId();
    }

    public String getRandomLobbyId(String excludedLobbyId) {
        List<CloudServer> lobbyServer = new ArrayList<>(getCloudServerGroups().get("LOBBY").getServers());
        for(CloudServer lb : lobbyServer)
            if(lb.getServerId().equals(excludedLobbyId)) lobbyServer.remove(lb);

        int i = ThreadLocalRandom.current().nextInt(lobbyServer.size());

        return lobbyServer.get(i).getServerId();
    }

    public String getUniqueServerId(String serverName) {
        return ServerIdResolver.getUniqueServerId(serverName, getCloudServers().keySet());
    }

    public int connect(String server, ProxiedPlayer proxiedPlayer) {
        ServerInfo serverInfo = ByteCloudMaster.getInstance().getProxy().getServerInfo(server);

        if (serverInfo == null)
            return 2;

        if (proxiedPlayer.getServer().getInfo() == serverInfo)
            return 1;

        proxiedPlayer.connect(serverInfo);
        return 0;
    }

    public int move(ProxiedPlayer proxiedPlayer, ProxiedPlayer target) {
        if (target == null)
            return 2;

        ServerInfo serverInfo = target.getServer().getInfo();

        if (proxiedPlayer.getServer().getInfo() == serverInfo)
            return 1;

        proxiedPlayer.connect(serverInfo);
        return 0;
    }
}
