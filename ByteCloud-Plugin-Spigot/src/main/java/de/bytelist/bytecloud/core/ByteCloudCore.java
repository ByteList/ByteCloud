package de.bytelist.bytecloud.core;

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import de.bytelist.bytecloud.command.GoToCommand;
import de.bytelist.bytecloud.command.JoinCommand;
import de.bytelist.bytecloud.command.ServerCommand;
import de.bytelist.bytecloud.command.StopCommand;
import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.CloudPermissionCheck;
import de.bytelist.bytecloud.common.packet.client.ClientServerStartedPacket;
import de.bytelist.bytecloud.common.spigot.SpigotCloudAPI;
import de.bytelist.bytecloud.common.spigot.SpigotCloudPlugin;
import de.bytelist.bytecloud.config.CloudConfig;
import de.bytelist.bytecloud.core.cloud.CloudHandler;
import de.bytelist.bytecloud.packet.ByteCloudPacketProtocol;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.Base64;

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
    private Session session;
    @Getter
    private CloudConfig cloudConfig;
    @Getter
    private File configFile;

    @Getter @Setter
    private CloudPermissionCheck<Player> permissionCheck;

    @Getter
    private String serverId = "unknown", version = "unknown";

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

        this.serverId = this.cloudHandler.getServerId();

        byte[] decodedKey = Base64.getDecoder().decode(System.getProperty("de.bytelist.bytecloud.communication", "null"));
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        Client packetClient = new Client("127.0.0.1", this.cloudHandler.getSocketPort(), new ByteCloudPacketProtocol(key), new TcpSessionFactory());
        this.session = packetClient.getSession();
        this.session.connect();
        this.session.send(new ClientServerStartedPacket(this.serverId));

        Bukkit.getConsoleSender().sendMessage(Cloud.PREFIX + "§aEnabled!");

        getCommand("goto").setExecutor(new GoToCommand());
        getCommand("join").setExecutor(new JoinCommand());
        getCommand("server").setExecutor(new ServerCommand());
        getCommand("stop").setExecutor(new StopCommand());
        getCommand("cloud").setExecutor((sender, cmd, label, args) -> {
            String[] version = this.version.split(":");
            sender.sendMessage("This server is running ByteCloud version "+version[0]+" (Git: "+version[1]+", by ByteList)");
            return true;
        });

        this.cloudAPI = new ByteSpigotCloudAPI();
    }

    @Override
    public void onDisable() {
        this.session.disconnect("Plugin disabled.");
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
