package de.bytelist.bytecloud.command;

import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.spigot.SpigotCloud;
import de.bytelist.bytecloud.common.spigot.SpigotCloudPlugin;
import org.bukkit.Bukkit;
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

    private final SpigotCloudPlugin spigotCloudPlugin = SpigotCloud.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        throw new UnsupportedOperationException("Not supported yet.");
//        if(!(sender instanceof Player)) {
//            sender.sendMessage("You can't connect to other servers!");
//            return true;
//        }
//        Player player = (Player) sender;
//
//        if(!spigotCloudPlugin.getPermissionCheck().hasPermission("command.goto", player)) {
//            sender.sendMessage(spigotCloudPlugin.getPermissionCheck().getNoPermissionMessage());
//            return true;
//        }
//
//        if (args.length == 1) {
//            String playername = args[0];
//            UUID uuid = spigotCloudPlugin.getCloudAPI().getUniqueIdFromName(playername);
//
//            if(uuid == null) {
//                sender.sendMessage(Cloud.PREFIX + "§cCan't find online player called "+playername);
//                return true;
//            }
//
//            spigotCloudPlugin.getCloudAPI().movePlayerToServer(player.getUniqueId(), spigotCloudPlugin.getCloudAPI().getServerIdFromPlayer(uuid));
//
//            int con = ByteCloudMaster.getInstance().getCloudHandler().move(pp, target);
//
//            if (con == 0) {
//                pp.sendMessage(ByteCloudMaster.getInstance().prefix + "§7Verbinde zum Server " + target.getServer().getInfo().getName() + "...");
//                return;
//            }
//            if (con == 1) {
//                pp.sendMessage(ByteCloudMaster.getInstance().prefix + "§cDu befindest dich schon auf diesem Server!");
//                return;
//            }
//            if (con == 2) {
//                pp.sendMessage(ByteCloudMaster.getInstance().prefix + "§c" + playername + " ist nicht online!");
//                return;
//            }
//            pp.sendMessage(ByteCloudMaster.getInstance().prefix + "§cError: Konnte keine richtige ID finden: " + con);
//            return;
//        }
//        pp.sendMessage(ByteCloudMaster.getInstance().prefix + "§c/goto <Spieler>");
//
//
//
//        return false;
    }
}
