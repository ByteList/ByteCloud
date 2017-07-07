package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.bungee.cloud.CloudHandler;
import de.bytelist.bytecloud.bungee.listener.LoginListener;
import de.bytelist.bytecloud.bungee.listener.ServerConnectListener;
import de.bytelist.bytecloud.bungee.properties.CloudProperties;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.bungee.BungeeClient;
import de.bytelist.bytecloud.network.bungee.packet.PacketInBungee;
import de.bytelist.bytecloud.network.bungee.packet.PacketInBungeeStopped;
import lombok.Getter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * Created by ByteList on 27.01.2017.
 */
public class ByteCloudMaster extends Plugin {

    public String prefix = "§bCloud §8\u00BB ";

    @Getter
    private final String version = "2.1";
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

        getProxy().getPluginManager().registerListener(this, new LoginListener());
        getProxy().getPluginManager().registerListener(this, new ServerConnectListener());

        NetworkManager.connect(Integer.valueOf(CloudProperties.getCloudProperties().getProperty("socket-port", "4213")), getLogger());
        this.bungeeClient = new BungeeClient();
        this.bungeeClient.z();

        getProxy().getConsole().sendMessage(prefix+"§aEnabled!");

        PacketInBungee packetInBungee = new PacketInBungee(this.cloudHandler.getBungeeId(), 25565);
        this.bungeeClient.sendPacket(packetInBungee);

        getProxy().getPluginManager().registerCommand(this, new Command("bytecloud", null, "cloud", "cloudsystem", "bungeecloud") {
            @Override
            public void execute(CommandSender sender, String[] args) {
                sender.sendMessage(ByteCloudMaster.getInstance().prefix+"§fByteCloud: v"+cloudHandler.getCloudVersion()+", " +
                        "Bungee: v"+version+", Spigot: v"+version+" (Started: "+cloudHandler.getCloudStarted()+", by ByteList)");
            }
        });
    }

    @Override
    public void onDisable() {
        this.bungeeClient.sendPacket(new PacketInBungeeStopped(cloudHandler.getBungeeId()));
        getProxy().getConsole().sendMessage(prefix+"§cDisabled!");
    }
}
