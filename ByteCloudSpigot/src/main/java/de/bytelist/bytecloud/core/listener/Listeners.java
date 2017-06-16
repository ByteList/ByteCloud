package de.bytelist.bytecloud.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by ByteList on 20.12.2016.
 */
public class Listeners implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent e) {
        e.setMaxPlayers(0);
        e.setMotd("§cServer created with ByteCloud.\n§7\u00BB ByteCloud-System");
    }
//
//    @EventHandler
//    public void onLogin(PlayerLoginEvent e) {
//        Player p = e.getPlayer();
//
//        String before = "bo"+GCGeneral.getSqlHandler().getOnline("UUID", p.getUniqueId().toString());
//        if(GCGeneral.getSqlHandler().getOnline("UUID", p.getUniqueId().toString()) == 0) {
//            String code = "af"+GCGeneral.getSqlHandler().getOnline("UUID", p.getUniqueId().toString())+before;
//
//            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "\n"+
//                    ByteCloudCore.getInstance().prefix+"§cBitte logge dich über Game-Chest.de ein!" +
//                    "\n§7Fehler-Code: §e"+code);
//        }
//    }
}
