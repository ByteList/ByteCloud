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
                e.setKickMessage("§4Der Cloud-Server kann nicht erreicht werden.");
                e.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            }

            @EventHandler
            public void onPing(ServerListPingEvent e) {
                e.setMotd("§6Game-Chest§f.§6de §7\u00BB §eSurvival §f& §eSpielmodi §8[§b1.9 §f- §c1.12§8]\n§cDer Cloud-Server kann ist momentan offline");
                e.setMaxPlayers(0);
            }

        }, this);

        getServer().getConsoleSender().sendMessage(prefix + "§aEnabled!");
    }

    @Override
    public void onDisable() {

        getServer().getConsoleSender().sendMessage(prefix + "§cDisabled!");
    }
}