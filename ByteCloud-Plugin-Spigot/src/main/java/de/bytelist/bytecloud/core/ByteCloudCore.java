package de.bytelist.bytecloud.core;

import de.bytelist.bytecloud.core.cloud.CloudAPI;
import de.bytelist.bytecloud.core.cloud.CloudHandler;
import de.bytelist.bytecloud.core.properties.CloudProperties;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.server.ServerClient;
import de.bytelist.bytecloud.network.server.packet.PacketInServer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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

    private String version = "unknown";

    @Override
    public void onEnable() {
        instance = this;

        // 2.0-23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = ByteCloudCore.class.getPackage().getImplementationVersion().split(":");
        // 2.0-23:003425
        version = v[0]+":"+v[1].substring(0, 6);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        CloudProperties.load();
        cloudHandler = new CloudHandler();
        cloudAPI = new CloudAPI();

        NetworkManager.connect(Integer.valueOf(CloudProperties.getCloudProperties().getProperty("socket-port", "4213")), getLogger());
        this.serverClient = new ServerClient();
        this.serverClient.connect();

        Bukkit.getConsoleSender().sendMessage(prefix + "§aEnabled!");

        String serverId = cloudHandler.getServerId();

        PacketInServer packetInServer = new PacketInServer(serverId);
        this.serverClient.sendPacket(packetInServer);

        getCommand("cloud").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage(prefix+"§fSpigot: v"+version);
            sender.sendMessage(prefix+"§fCloud started: "+cloudHandler.getCloudStarted()+", developed by ByteList");
            return true;
        });
    }

    @Override
    public void onDisable() {
        this.serverClient.disconnect();
        Bukkit.getConsoleSender().sendMessage(prefix + "§cDisabled!");
    }

    public String getVersion() {
        return version;
    }
}
