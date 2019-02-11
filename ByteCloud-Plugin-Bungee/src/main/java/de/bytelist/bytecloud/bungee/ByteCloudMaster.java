package de.bytelist.bytecloud.bungee;

import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import de.bytelist.bytecloud.bungee.cloud.CloudHandler;
import de.bytelist.bytecloud.bungee.listener.LoginListener;
import de.bytelist.bytecloud.bungee.listener.ServerConnectListener;
import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.CloudPermissionCheck;
import de.bytelist.bytecloud.common.bungee.BungeeCloudAPI;
import de.bytelist.bytecloud.common.bungee.BungeeCloudPlugin;
import de.bytelist.bytecloud.config.CloudConfig;
import de.bytelist.bytecloud.packet.ByteCloudPacketProtocol;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.util.Base64;

/**
 * Created by ByteList on 27.01.2017.
 */
public class ByteCloudMaster extends Plugin implements BungeeCloudPlugin {

    @Getter
    private String serverId = "unknown", version = "unknown";
    @Getter
    private static ByteCloudMaster instance;
    @Getter
    private CloudHandler cloudHandler;
    @Getter
    private Client packetClient;
    @Getter
    private String forcedJoinServerId;
    @Getter
    private CloudConfig cloudConfig;
    @Getter
    private File configFile;
    @Getter
    private BungeeCloudAPI cloudAPI;
    @Getter @Setter
    private CloudPermissionCheck<ProxiedPlayer> permissionCheck;

    @Override
    public void onEnable() {
        Cloud.setInstance(instance = this);

        this.configFile = new File("plugins/ByteCloud", "config.json");
        this.cloudConfig = CloudConfig.loadDocument(this.configFile);

        this.cloudHandler = new CloudHandler();

        this.serverId = this.cloudHandler.getBungeeId();

        // 2.0-23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = ByteCloudMaster.class.getPackage().getImplementationVersion().split(":");
        // 2.0-23:0034258
        version = v[0]+":"+v[1].substring(0, 7);

        this.forcedJoinServerId = System.getProperty("de.bytelist.bytecloud.connectServer", "-1");

        this.permissionCheck = new PermissionCheck();

        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerListener(this, new ServerConnectListener());

        byte[] decodedKey = Base64.getDecoder().decode(System.getProperty("de.bytelist.bytecloud.communication", "null"));
        SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        this.packetClient = new Client("127.0.0.1", this.cloudHandler.getSocketPort(), new ByteCloudPacketProtocol(key), new TcpSessionFactory());
        this.packetClient.getSession().connect();

        getProxy().getConsole().sendMessage(Cloud.PREFIX+"§aEnabled!");

        getProxy().getPluginManager().registerCommand(this, new Command("cloudend") {
            @Override
            public void execute(CommandSender sender, String[] args) {
                if(sender instanceof ProxiedPlayer)
                    sender.sendMessage("§bCloud §8\u00BB §cDu hast keine Berechtigung für diesen Befehl!");
                else
                    getProxy().stop("§bCloud §8\u00BB §cDer Cloud-Server konnte nicht erreicht werden.\n" +
                            "§7Grund: Cloud wird neu gestartet.");
            }
        });

        this.cloudAPI = new ByteBungeeCloudAPI();
    }

    @Override
    public void onDisable() {
        this.packetClient.getSession().disconnect("Plugin disabled.");
        getProxy().getConsole().sendMessage(Cloud.PREFIX+"§cDisabled!");
        super.onDisable();
    }

    public static class PermissionCheck implements CloudPermissionCheck<ProxiedPlayer> {

        @Override
        public boolean hasPermission(String permission, ProxiedPlayer checker) {
            return checker.hasPermission("cloud.admin");
        }

        @Override
        public String getNoPermissionMessage() {
            return "§cYou don't have the permission for this command!";
        }
    }
}
