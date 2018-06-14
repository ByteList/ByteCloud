package de.bytelist.bytecloud.bungee.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.network.bungee.PacketInPlayerChangedServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by ByteList on 29.01.2017.
 */
public class ServerConnectListener implements Listener {

    private final ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        Server from = pp.getServer();
        boolean newConnection = false;

        if(from == null) {
            newConnection = true;
            String serverId = byteCloudMaster.getServerIdOnConnect();
            if(byteCloudMaster.getProxy().getServerInfo(serverId) != null) {
                e.setTarget(byteCloudMaster.getProxy().getServerInfo(serverId));
            } else {
                e.setTarget(byteCloudMaster.getProxy().getServerInfo(byteCloudMaster.getCloudHandler().getRandomLobbyId()));
            }
        }

        byteCloudMaster.getBungeeClient().sendPacket(new PacketInPlayerChangedServer(pp.getName(), (newConnection ? "null" : from.getInfo().getName()), e.getTarget().getName()));
    }


}
