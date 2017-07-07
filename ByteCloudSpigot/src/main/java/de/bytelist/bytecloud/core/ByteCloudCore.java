package de.bytelist.bytecloud.core;

import de.bytelist.bytecloud.core.cloud.CloudAPI;
import de.bytelist.bytecloud.core.cloud.CloudHandler;
import de.bytelist.bytecloud.core.listener.Listeners;
import de.bytelist.bytecloud.core.properties.CloudProperties;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.server.ServerClient;
import de.bytelist.bytecloud.network.server.packet.PacketInServer;
import de.bytelist.bytecloud.network.server.packet.PacketInServerStopped;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by ByteList on 20.12.2016.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudCore extends JavaPlugin {

    @Getter
    private static ByteCloudCore instance;
    @Getter
    private CloudHandler cloudHandler;
    @Getter
    private CloudAPI cloudAPI;
    @Getter
    private ServerClient serverClient;

    public String prefix = "§bCloud §8\u00BB ";

    @Override
    public void onEnable() {
        instance = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");


        CloudProperties.load();
        cloudHandler = new CloudHandler();
        cloudAPI = new CloudAPI();

        NetworkManager.connect(Integer.valueOf(CloudProperties.getCloudProperties().getProperty("socket-port", "4213")), getLogger());
        this.serverClient = new ServerClient();
        this.serverClient.z();

        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
        Bukkit.getConsoleSender().sendMessage(prefix + "§aEnabled!");

        String serverId = cloudHandler.getServerId();

        PacketInServer packetInServer = new PacketInServer(serverId);
        this.serverClient.sendPacket(packetInServer);
    }

    @Override
    public void onDisable() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            String lb = this.cloudHandler.getRandomLobbyId(this.cloudHandler.getServerId());
            this.cloudAPI.moveToServer(player, lb);
        }
        this.serverClient.sendPacket(new PacketInServerStopped(cloudHandler.getServerId()));
        Bukkit.getConsoleSender().sendMessage(prefix + "§cDisabled!");
    }
}
