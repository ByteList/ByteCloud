package de.bytelist.bytecloud.command;

import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.bytelist.bytecloud.common.spigot.SpigotCloudPlugin;
import de.bytelist.bytecloud.core.ByteCloudCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by nemmerich on 07.02.2019.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class JoinCommand implements CommandExecutor {

    private final SpigotCloudPlugin spigotCloudPlugin = ByteCloudCore.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("You can't connect to other servers!");
            return true;
        }
        Player player = (Player) sender;
        CloudPlayer cloudPlayer = spigotCloudPlugin.getCloudAPI().getPlayer(player.getUniqueId());

        if(!spigotCloudPlugin.getPermissionCheck().hasPermission("command.join", player)) {
            sender.sendMessage(spigotCloudPlugin.getPermissionCheck().getNoPermissionMessage());
            return true;
        }

        if (args.length == 1) {
            String serverId = spigotCloudPlugin.getCloudAPI().getUniqueServerId(args[0]);

            if(serverId == null) {
                sender.sendMessage(Cloud.PREFIX + "§cCan't find server called "+args[0]);
                return true;
            }

            CloudServer targetServer = spigotCloudPlugin.getCloudAPI().getServer(serverId);

            if(targetServer== null) {
                player.sendMessage(Cloud.PREFIX+"The server of "+args[0]+" is null!");
                return true;
            }

            if(cloudPlayer.getCurrentServer() == targetServer) {
                player.sendMessage(Cloud.PREFIX+"§cYou're currently on this server!");
                return true;
            }

            player.sendMessage(Cloud.PREFIX + "§7Connecting to " + targetServer.getServerId() + "...");
            spigotCloudPlugin.getCloudAPI().movePlayerToServer(player.getUniqueId(), targetServer.getServerId());
            return true;
        }

        player.sendMessage(Cloud.PREFIX+"§c/join <server>");
        return true;
    }
}
