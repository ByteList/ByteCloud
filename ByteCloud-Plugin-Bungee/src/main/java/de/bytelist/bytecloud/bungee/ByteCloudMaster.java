package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.bungee.cloud.CloudHandler;
import de.bytelist.bytecloud.bungee.listener.ChatListener;
import de.bytelist.bytecloud.bungee.listener.LoginListener;
import de.bytelist.bytecloud.bungee.listener.ServerConnectListener;
import de.bytelist.bytecloud.bungee.properties.CloudProperties;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.bungee.BungeeClient;
import de.bytelist.bytecloud.network.bungee.packet.PacketInBungee;
import de.bytelist.bytecloud.network.bungee.packet.PacketInBungeeStopped;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created by ByteList on 27.01.2017.
 */
public class ByteCloudMaster extends Plugin {

    public String prefix = "§bCloud §8\u00BB ";

    @Getter
    private String version = "unknown";
    @Getter
    private static ByteCloudMaster instance;
    @Getter
    private CloudHandler cloudHandler;
    @Getter
    private BungeeClient bungeeClient;

    @Override
    public void onEnable() {
        instance = this;
        CloudProperties.load();
        this.cloudHandler = new CloudHandler();

        // 2.0-23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = ByteCloudMaster.class.getPackage().getImplementationVersion().split(":");
        // 2.0-23:003425
        version = v[0]+":"+v[1].substring(0, 6);

        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerListener(this, new ServerConnectListener());
        getProxy().getPluginManager().registerListener(this, new ChatListener());

        NetworkManager.connect(Integer.valueOf(CloudProperties.getCloudProperties().getProperty("socket-port", "4213")), getLogger());
        this.bungeeClient = new BungeeClient();
        this.bungeeClient.connect();

        getProxy().getConsole().sendMessage(prefix+"§aEnabled!");

        PacketInBungee packetInBungee = new PacketInBungee(this.cloudHandler.getBungeeId(), 25565);
        this.bungeeClient.sendPacket(packetInBungee);

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
    }

    @Override
    public void onDisable() {
        this.bungeeClient.sendPacket(new PacketInBungeeStopped(cloudHandler.getBungeeId()));
        this.bungeeClient.disconnect();
        getProxy().getConsole().sendMessage(prefix+"§cDisabled!");
    }
}
