package de.bytelist.bytecloud.core;

import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.CloudPermissionCheck;
import de.bytelist.bytecloud.common.spigot.SpigotCloudAPI;
import de.bytelist.bytecloud.common.spigot.SpigotCloudPlugin;
import de.bytelist.bytecloud.config.CloudConfig;
import de.bytelist.bytecloud.core.cloud.CloudHandler;
import de.bytelist.bytecloud.packet.ByteCloudPacketClient;
import de.bytelist.bytecloud.packet.NetworkManager;
import de.bytelist.bytecloud.packet.server.PacketInServer;
import de.bytelist.bytecloud.packet.server.ServerClient;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by ByteList on 20.12.2016.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudCore extends JavaPlugin implements SpigotCloudPlugin {

    @Getter
    private static ByteCloudCore instance;
    @Getter
    private CloudHandler cloudHandler;
    @Getter
    private SpigotCloudAPI cloudAPI;
    @Getter
    private ByteCloudPacketClient packetClient;
    @Getter
    private CloudConfig cloudConfig;
    @Getter
    private File configFile;

    @Getter @Setter
    private CloudPermissionCheck<Player> permissionCheck;

    @Getter
    private String version = "unknown";

    @Override
    public void onEnable() {
        Cloud.setInstance(instance = this);
        this.permissionCheck = new PermissionCheck();

        // 2.0-23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = ByteCloudCore.class.getPackage().getImplementationVersion().split(":");
        // 2.0-23:0034258
        version = v[0]+":"+v[1].substring(0, 7);

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.configFile = new File("plugins/ByteCloud", "config.json");
        this.cloudConfig = CloudConfig.loadDocument(this.configFile);

        this.cloudHandler = new CloudHandler();

        this.packetClient = new ByteCloudPacketClient(this.cloudHandler.getSocketPort());
        this.packetClient.connect();

        Bukkit.getConsoleSender().sendMessage(Cloud.PREFIX + "§aEnabled!");

        String serverId = cloudHandler.getServerId();

        PacketInServer packetInServer = new PacketInServer(serverId);
        this.packetClient.sendPacket(packetInServer);

        getCommand("cloud").setExecutor((sender, cmd, label, args) -> {
            sender.sendMessage("This server is running ByteCloud version "+version+" (by ByteList, Started: "+cloudHandler.getCloudStarted()+")");
            return true;
        });

        this.cloudAPI = new ByteSpigotCloudAPI();
    }

    @Override
    public void onDisable() {
        this.packetClient.disconnect();
        Bukkit.getConsoleSender().sendMessage(Cloud.PREFIX + "§cDisabled!");
    }

    public static class PermissionCheck implements CloudPermissionCheck<Player> {

        @Override
        public boolean hasPermission(String permission, Player checker) {
            return checker.isOp();
        }

        @Override
        public String getNoPermissionMessage() {
            return "§cYou don't have the permission for this command!";
        }
    }
}
