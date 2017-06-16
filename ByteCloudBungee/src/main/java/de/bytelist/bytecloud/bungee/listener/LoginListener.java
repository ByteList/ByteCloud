package de.bytelist.bytecloud.bungee.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by ByteList on 29.01.2017.
 */
public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent e) {
        PendingConnection pc = e.getConnection();

//        if(!ByteCloudMaster.getInstance().getCloudHandler().canLogin()) {
//            e.setCancelled(true);
//            e.setCancelReason(ByteCloudMaster.getInstance().prefix+"§cDer Cloud-Server kann nicht erreicht werden.");
//        }

        if(pc.getVersion() < 100) {
            e.setCancelled(true);
            e.setCancelReason(ByteCloudMaster.getInstance().prefix+"§cBitte verwende eine Minecraft-Version ab 1.9!");
        }
    }
}
