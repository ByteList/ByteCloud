package de.bytelist.bytecloud.core.packet;

import com.google.gson.JsonObject;
import com.voxelboxstudios.resilent.client.JsonClientListener;
import de.bytelist.bytecloud.core.ByteCloudCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Created by ByteList on 02.02.2017.
 */
public class PacketListener extends JsonClientListener {

    @Override
    public void jsonReceived(JsonObject paramJsonObject) {
        if(paramJsonObject.has("packet") && paramJsonObject.has("try")) {
            String packet = paramJsonObject.get("packet").getAsString();
            String tryy = paramJsonObject.get("try").getAsString();

            if (packet.equals("SERVER") && tryy.equals("STOP")) {
                ByteCloudCore.getInstance().getCloudHandler().removeServerFromDatabase(ByteCloudCore.getInstance().getCloudHandler().getServerId());

                for (final Player player : Bukkit.getOnlinePlayers()) {
                        int i = ByteCloudCore.getInstance().getCloudAPI().moveToLobby(player);
                        if(i == 0) player.sendMessage(ByteCloudCore.getInstance().prefix +
                                "§cDein aktueller Server wurde gestoppt!");
                        else Bukkit.getScheduler().runTaskAsynchronously(ByteCloudCore.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                player.kickPlayer(ByteCloudCore.getInstance().prefix+"§cDu konntest auf keinen Lobby-Server verschoben werden!\n\n§7Grund: §eLobby-Group stopped!");
                            }
                        });
                }
                try {
                    Thread.sleep(1500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Bukkit.getServer().shutdown();
            }
        }
    }

    @Override
    public void disconnected() {
        Bukkit.getLogger().info("[PacketServer/Cl] Disconnected from ByteCloud.");
    }

    @Override
    public void connected() {

        Bukkit.getLogger().info("[PacketServer/Cl] Connected to ByteCloud as server.");
    }
}
