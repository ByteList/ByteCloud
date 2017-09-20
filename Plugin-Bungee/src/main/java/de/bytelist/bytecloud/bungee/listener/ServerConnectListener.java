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
            String randomLobby = ByteCloudMaster.getInstance().getCloudHandler().getRandomLobbyId();
            if(randomLobby != null)
                e.setTarget(ByteCloudMaster.getInstance().getProxy().getServerInfo(randomLobby));
            else {
                e.setCancelled(true);
            }
        }
    }
}
