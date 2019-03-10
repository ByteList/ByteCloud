package de.bytelist.bytecloud.bungee.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.common.packet.client.player.ClientPlayerServerSwitchPacket;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Created by ByteList on 29.01.2017.
 */
public class ServerConnectListener implements Listener {

    private final ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();

    @EventHandler(priority = EventPriority.LOW)
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        Server from = pp.getServer();

        if(from == null) {
            String serverId = byteCloudMaster.getForcedJoinServerId();
            if(byteCloudMaster.getProxy().getServerInfo(serverId) != null) {
                e.setTarget(byteCloudMaster.getProxy().getServerInfo(serverId));
            } else {
                e.setTarget(byteCloudMaster.getProxy().getServerInfo(byteCloudMaster.getCloudHandler().getRandomLobbyId()));
            }
        }

        byteCloudMaster.getSession().send(new ClientPlayerServerSwitchPacket(pp.getUniqueId(), e.getTarget().getName()));
    }


}
