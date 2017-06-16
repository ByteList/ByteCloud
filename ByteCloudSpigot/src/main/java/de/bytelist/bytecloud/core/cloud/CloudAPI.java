package de.bytelist.bytecloud.core.cloud;

import de.bytelist.bytecloud.core.ByteCloudCore;
import de.bytelist.bytecloud.database.DatabaseServerObject;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.server.packet.PacketInStopOwnServer;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ByteList on 20.12.2016.
 */
public class CloudAPI {
    /**
     * Gets all active server id's in a string list.
     *
     * @return StringList - server id
     */
    private List<String> getServer() {
        return ByteCloudCore.getInstance().getCloudHandler().getServerInDatabase();
    }

    /**
     * Gets a map sorted by port that returns the matching server id.
     *
     * @return HashMap - port, server id
     */
    private HashMap<Integer, String> getPorts() {
        HashMap<Integer, String> ports = new HashMap<>();

        for(String server : getServer()) {
            Integer port = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(server, DatabaseServerObject.PORT).getAsInt();
            ports.put(port, server);
        }

        return ports;
    }

    /**
     * Returns the id from this server.
     *
     * @return String - server id
     */
    public String getServerId() {
        return ByteCloudCore.getInstance().getCloudHandler().getServerId();
    }

    /**
     * If you have a server name for example lb-01
     * you can get the id from this server.
     *
     * @param serverName
     * @return String - server id
     */
    public String getServerUniqueId(String serverName) {
        return ByteCloudCore.getInstance().getCloudHandler().getUniqueServerId(serverName);
    }


    /**
     * Add a playername to the player list in database and
     * add 1 to the online player value.
     *
     * @param player
     */
    public void addPlayer(Player player) {
        String serverId = ByteCloudCore.getInstance().getCloudHandler().getServerId();
        Integer value = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt()+1;
        String connectedPlayer = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.PLAYERS).getAsString();

        connectedPlayer = connectedPlayer+(player.getName()+",");

        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.PLAYER_ONLINE, value);
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.PLAYERS, connectedPlayer);
    }

    /**
     * Remove a playername from the player list in database and
     * remove 1 from the online player value.
     *
     * @param player
     */
    public void removePlayer(Player player) {
        String serverId = ByteCloudCore.getInstance().getCloudHandler().getServerId();
        Integer value = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.PLAYER_ONLINE).getAsInt()-1;
        String connectedPlayer =  ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.PLAYERS).getAsString();

        connectedPlayer = connectedPlayer.replace(player.getName()+",", "");

        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.PLAYER_ONLINE, value);
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.PLAYERS, connectedPlayer);
    }

    /**
     * Add a playername to the spectator list in database and
     * add 1 to the online spectator value.
     *
     * @param spectator
     */
    public void addSpectator(Player spectator) {
        String serverId = ByteCloudCore.getInstance().getCloudHandler().getServerId();
        Integer value = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt()+1;
        String connectedPlayer = ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.SPECTATORS).getAsString();

        connectedPlayer = connectedPlayer+(spectator.getName()+",");

        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.SPECTATOR_ONLINE, value);
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.SPECTATORS, connectedPlayer);
    }

    /**
     * Remove a playername from the spectator list in database and
     * remove 1 from the online spectator value.
     *
     * @param spectator
     */
    public void removeSpectator(Player spectator) {
        String serverId = ByteCloudCore.getInstance().getCloudHandler().getServerId();
        Integer value = (Integer) ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.SPECTATOR_ONLINE).getAsInt()-1;
        String connectedPlayer = (String) ByteCloudCore.getInstance().getCloudHandler().getDatabaseServerValue(serverId, DatabaseServerObject.SPECTATORS).getAsString();

        connectedPlayer = connectedPlayer.replace(spectator.getName()+",", "");

        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.SPECTATOR_ONLINE, value);
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(serverId, DatabaseServerObject.SPECTATORS, connectedPlayer);
    }

    /**
     * If you'll move a player from this server or
     * what ever to a lobby, you can use that method.
     * <p>
     * The player will be moved to a random online lobby server.
     *
     * @param player
     */
    public int moveToLobby(Player player) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(ByteCloudCore.getInstance().getCloudHandler().getRandomLobbyId());
            player.sendPluginMessage(ByteCloudCore.getInstance(), "BungeeCord", b.toByteArray());
            return 0;
        } catch (IOException e) {
            return 2;
        }
    }

    /**
     * If you'll move a player from this server to an other,
     * you can use this method.
     *
     * @param player
     * @param serverId
     */
    public void moveToServer(Player player, String serverId) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(serverId);
            player.sendPluginMessage(ByteCloudCore.getInstance(), "BungeeCord", b.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If this server is a game server you can change the server state.
     * That will be displayed at the server signs.
     *
     * You can use these server states:
     *      LOBBY, FULL, INGAME, RESTART, WHITELIST
     *
     * @param serverState
     */
    public void changeServerState(ServerState serverState) {
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(getServerId(), DatabaseServerObject.STATE,
                serverState.toString());
    }

    /**
     * If this server is a game server you can change the server motd.
     * That will be displayed at the server signs.
     *
     * @param motd
     */
    public void setMotd(String motd) {
        ByteCloudCore.getInstance().getCloudHandler().editDatabaseServerValue(getServerId(), DatabaseServerObject.MOTD,
                motd);
    }

    public void shutdown() {
        PacketInStopOwnServer packetInStopOwnServer = new PacketInStopOwnServer(getServerId());
        NetworkManager.getServerClient().sendPacket(packetInStopOwnServer);
    }

    /**
     * An enum with all server states for the cloud server signs.
     */
    public enum ServerState {
        STARTING, LOBBY, FULL, INGAME, RESTART, WHITELIST
    }
}
