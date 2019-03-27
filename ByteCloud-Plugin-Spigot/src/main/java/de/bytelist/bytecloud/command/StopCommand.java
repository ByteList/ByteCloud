package de.bytelist.bytecloud.command;

import de.bytelist.bytecloud.common.spigot.SpigotCloud;
import de.bytelist.bytecloud.common.spigot.SpigotCloudPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by nemmerich on 19.03.2019.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class StopCommand implements CommandExecutor {
    private final SpigotCloudPlugin spigotCloudPlugin = SpigotCloud.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("You can't connect to other servers!");
            return true;
        }
        Player player = (Player) sender;

        if(!spigotCloudPlugin.getPermissionCheck().hasPermission("command.stop", player)) {
            sender.sendMessage(spigotCloudPlugin.getPermissionCheck().getNoPermissionMessage());
            return true;
        }

        spigotCloudPlugin.getCloudAPI().shutdown();
        return true;
    }
}
