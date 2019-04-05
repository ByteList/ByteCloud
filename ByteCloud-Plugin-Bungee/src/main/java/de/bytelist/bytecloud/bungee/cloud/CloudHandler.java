package de.bytelist.bytecloud.bungee.cloud;

import de.bytelist.bytecloud.CloudAPIHandler;
import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.common.packet.cloud.CloudServerStartedPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerKickPacket;
import de.bytelist.bytecloud.common.packet.cloud.player.CloudPlayerMessagePacket;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;

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
}
