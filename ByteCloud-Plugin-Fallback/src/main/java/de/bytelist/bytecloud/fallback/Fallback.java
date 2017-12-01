package de.bytelist.bytecloud.fallback;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by ByteList on 01.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Fallback extends JavaPlugin {

    @Getter
    private static Fallback instance;


    public final String prefix = "§bByteCloud §8\u00BB ";

    @Override
    public void onEnable() {
        instance = this;

        getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onLogin(PlayerLoginEvent e) {
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(prefix+"§cDer Cloud-Server konnte nicht erreicht werden.");
            }

            @EventHandler
            public void onPing(ServerListPingEvent e) {
                e.setMotd("§6Game-Chest.de §7\u00BB §cKeine Verbindung");
            }

        }, this);

        getServer().getConsoleSender().sendMessage(prefix + "§aEnabled!");
    }

    @Override
    public void onDisable() {

        getServer().getConsoleSender().sendMessage(prefix + "§cDisabled!");
    }
}