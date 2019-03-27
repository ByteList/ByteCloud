package de.bytelist.bytecloud.command;

import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.CloudPlayer;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.bytelist.bytecloud.common.spigot.SpigotCloud;
import de.bytelist.bytecloud.common.spigot.SpigotCloudPlugin;
import de.bytelist.bytecloud.core.ByteCloudCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by nemmerich on 07.02.2019.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class GoToCommand implements CommandExecutor {

    private final SpigotCloudPlugin spigotCloudPlugin = ByteCloudCore.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("You can't connect to other servers!");
            return true;
        }
        Player player = (Player) sender;
        CloudPlayer cloudPlayer = spigotCloudPlugin.getCloudAPI().getPlayer(player.getUniqueId());

        if(!spigotCloudPlugin.getPermissionCheck().hasPermission("command.goto", player)) {
            sender.sendMessage(spigotCloudPlugin.getPermissionCheck().getNoPermissionMessage());
            return true;
        }

        if (args.length == 1) {
            String playername = args[0];
            UUID target = spigotCloudPlugin.getCloudAPI().getUniqueIdFromName(playername);

            if(target == null) {
                sender.sendMessage(Cloud.PREFIX + "§cCan't find online player called "+playername);
                return true;
            }

            CloudPlayer targetPlayer = spigotCloudPlugin.getCloudAPI().getPlayer(target);

            if(targetPlayer.getCurrentServer() == null) {
                player.sendMessage(Cloud.PREFIX+"The current server of "+targetPlayer.getName()+" is null!");
                return true;
            }

            if(cloudPlayer.getCurrentServer() == targetPlayer.getCurrentServer()) {
                player.sendMessage(Cloud.PREFIX+"§cYou're currently on this server!");
                return true;
            }

            player.sendMessage(Cloud.PREFIX + "§7Connecting to " + targetPlayer.getCurrentServer().getServerId() + "...");
            spigotCloudPlugin.getCloudAPI().movePlayerToServer(player.getUniqueId(), spigotCloudPlugin.getCloudAPI().getServerIdFromPlayer(target));
            return true;
        }

        player.sendMessage(Cloud.PREFIX+"§c/goto <playername>");
        return true;
    }
}
