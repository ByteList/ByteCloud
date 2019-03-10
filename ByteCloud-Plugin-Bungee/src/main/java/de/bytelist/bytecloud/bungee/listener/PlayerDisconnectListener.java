package de.bytelist.bytecloud.bungee.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import de.bytelist.bytecloud.common.packet.client.player.ClientPlayerDisconnectPacket;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by nemmerich on 10.03.2019.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        ByteCloudMaster.getInstance().getSession().send(new ClientPlayerDisconnectPacket(e.getPlayer().getUniqueId()));
    }
}
