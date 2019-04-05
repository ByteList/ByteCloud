package de.bytelist.bytecloud.command;

import com.google.common.base.Joiner;
import de.bytelist.bytecloud.common.Cloud;
import de.bytelist.bytecloud.common.packet.client.ClientServerStartPacket;
import de.bytelist.bytecloud.common.packet.client.ClientServerStopPacket;
import de.bytelist.bytecloud.common.server.CloudServer;
import de.bytelist.bytecloud.common.spigot.SpigotCloudPlugin;
import de.bytelist.bytecloud.core.ByteCloudCore;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nemmerich on 07.02.2019.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class ServerCommand implements CommandExecutor {

    private final SpigotCloudPlugin spigotCloudPlugin = ByteCloudCore.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(!spigotCloudPlugin.getPermissionCheck().hasPermission("command.server", player)) {
                sender.sendMessage(spigotCloudPlugin.getPermissionCheck().getNoPermissionMessage());
                return true;
            }
        }

        if (args.length == 1) {
            if(args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(Cloud.PREFIX+"§7Servers:");
                spigotCloudPlugin.getCloudAPI().getServers().forEach(cloudServer ->
                        sender.sendMessage("§8\u00BB §e"+cloudServer.getServerId()));
                return true;
            }
        }

        if(args.length == 2) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("Only can executed by player!");
                return true;
            }
            Player player = (Player) sender;
            if(!spigotCloudPlugin.getPermissionCheck().hasPermission("command.server.plus", player)) {
                sender.sendMessage(spigotCloudPlugin.getPermissionCheck().getNoPermissionMessage());
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "start":
                    ClientServerStartPacket clientServerStartPacket = new ClientServerStartPacket(args[1],
                            spigotCloudPlugin.getCloudAPI().getServerGroup(args[1]) != null, player.getUniqueId());
                    ByteCloudCore.getInstance().getSession().send(clientServerStartPacket);
                    return true;
                case "stop":
                    ClientServerStopPacket clientServerStopPacket = new ClientServerStopPacket(
                            spigotCloudPlugin.getCloudAPI().getUniqueServerId(args[0]),
                            player.getUniqueId());
                    ByteCloudCore.getInstance().getSession().send(clientServerStopPacket);
                    return true;
                case "info":
                    String id = spigotCloudPlugin.getCloudAPI().getUniqueServerId(args[1]);
                    CloudServer cloudServer = spigotCloudPlugin.getCloudAPI().getServer(id);

                    if(cloudServer == null) {
                        sender.sendMessage(Cloud.PREFIX+"§c Can't find a server called "+args[1]+"!");
                        return true;
                    }

                    List<String> players = new ArrayList<>();
                    cloudServer.getPlayers().forEach(cloudPlayer -> players.add(cloudPlayer.getName()));

                    TextComponent listPlayer = new TextComponent("§8\u00BB §7Players: §e"+
                            cloudServer.getPlayers().size()+"§7/§c"+cloudServer.getSlots());
                    listPlayer.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                            Joiner.on("\n").join(players)
                    ).create()));

                    sender.sendMessage("");
                    sender.sendMessage("§6Serverinformations §7(§e"+cloudServer.getServerId()+"§7)§6:");
                    sender.sendMessage("§8\u00BB §7Group: §a"+cloudServer.getServerGroup().getGroupName());
                    sender.sendMessage("§8\u00BB §7ServerState: §e"+cloudServer.getServerState().name());
                    sender.sendMessage("§8\u00BB §7Motd: "+cloudServer.getMotd());
                    player.spigot().sendMessage(listPlayer);

                    return true;
            }
        }

        sender.sendMessage(Cloud.PREFIX+"§c/server start <group/permServer>");
        sender.sendMessage(Cloud.PREFIX+"§c/server stop <server>");
        sender.sendMessage(Cloud.PREFIX+"§c/server info <server>");
        sender.sendMessage(Cloud.PREFIX+"§c/server list");
        return true;
    }
}
