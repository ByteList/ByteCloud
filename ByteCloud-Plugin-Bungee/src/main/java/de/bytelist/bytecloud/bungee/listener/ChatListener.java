package de.bytelist.bytecloud.bungee.listener;

import de.bytelist.bytecloud.bungee.ByteCloudMaster;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by ByteList on 30.10.17.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ChatListener implements Listener {

    private final ByteCloudMaster byteCloudMaster = ByteCloudMaster.getInstance();

    @EventHandler
    public void onChat(ChatEvent e) {
        if(e.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) e.getSender();
            String message = e.getMessage();
            if(message.startsWith("/")) {
                String command = message.split(" ")[0];

                if (command.equalsIgnoreCase("/cloud") || command.equalsIgnoreCase("/cloudsystem") || command.equalsIgnoreCase("/bytecloud")) {
                    player.sendMessage(byteCloudMaster.prefix + "ByteCloud: v"+byteCloudMaster.getCloudHandler().getCloudVersion());
                    player.sendMessage(byteCloudMaster.prefix + "Bungee: v"+byteCloudMaster.getVersion());
                }

                if(command.equalsIgnoreCase("/pl") || command.equalsIgnoreCase("/plugins")) {
                    e.setMessage("/fakeplugins s6adD4g146 exec");
                }
            }
        }
    }
}
