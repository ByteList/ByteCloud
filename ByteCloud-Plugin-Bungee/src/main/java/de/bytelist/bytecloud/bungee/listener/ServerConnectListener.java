package de.bytelist.bytecloud.bungee.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by ByteList on 29.01.2017.
 */
public class ServerConnectListener implements Listener {

    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer pp = e.getPlayer();
        Server from = pp.getServer();


        if(from == null) {
            String serverId;
            if(!ByteCloudMaster.getInstance().getServerIdOnConnect().equals("-1")) {
                serverId = ByteCloudMaster.getInstance().getServerIdOnConnect();
            } else {
                serverId = ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId();
            }

            if(serverId != null) {
                try {
                    e.setTarget(ByteCloudMaster.getInstance().getProxy().getServerInfo(serverId));
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }
}
