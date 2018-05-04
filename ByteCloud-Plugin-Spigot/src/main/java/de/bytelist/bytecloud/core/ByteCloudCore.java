package de.bytelist.bytecloud.core;

import de.bytelist.bytecloud.core.cloud.CloudAPI;
import de.bytelist.bytecloud.core.cloud.CloudHandler;
import de.bytelist.bytecloud.config.CloudConfig;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.server.PacketInServer;
import de.bytelist.bytecloud.network.server.ServerClient;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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
    @Getter
    private CloudConfig cloudConfig;
    @Getter
    private File configFile;

    public String prefix = "§bCloud §8\u00BB ";
    @Getter
    private String version = "unknown";

    @Override
    public void onEnable() {
        instance = this;

        // 2.0-23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = ByteCloudCore.class.getPackage().getImplementationVersion().split(":");
        // 2.0-23:0034258
        version = v[0]+":"+v[1].substring(0, 7);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.configFile = new File("plugins/ByteCloud", "cloudConfig.json");
        this.cloudConfig = CloudConfig.loadDocument(this.configFile);

        cloudHandler = new CloudHandler();
        cloudAPI = new CloudAPI();

        NetworkManager.connect(this.cloudHandler.getSocketPort(), getLogger());
        this.serverClient = new ServerClient();
        this.serverClient.connect();

        Bukkit.getConsoleSender().sendMessage(prefix + "§aEnabled!");

        String serverId = cloudHandler.getServerId();

        PacketInServer packetInServer = new PacketInServer(serverId);
        this.serverClient.sendPacket(packetInServer);

        getCommand("cloud").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage("This server is running ByteCloud version "+version+" (by ByteList, Started: "+cloudHandler.getCloudStarted()+")");
            return true;
        });
    }

    @Override
    public void onDisable() {
        this.serverClient.disconnect();
        Bukkit.getConsoleSender().sendMessage(prefix + "§cDisabled!");
    }
}
