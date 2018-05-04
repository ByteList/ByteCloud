package de.bytelist.bytecloud;

import de.bytelist.bytecloud.bungee.Bungee;
import de.bytelist.bytecloud.config.CloudConfig;
import de.bytelist.bytecloud.console.CommandHandler;
import de.bytelist.bytecloud.database.DatabaseManager;
import de.bytelist.bytecloud.database.DatabaseServer;
import de.bytelist.bytecloud.log.CloudLogger;
import de.bytelist.bytecloud.network.cloud.CloudServer;
import de.bytelist.bytecloud.server.ServerHandler;
import de.bytelist.bytecloud.server.screen.Screen;
import jline.console.ConsoleReader;

import java.io.File;

/**
 * Created by ByteList on 04.05.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface IByteCloud {

    /**
     * The ConsoleReader is used to optimize the console out- and input.
     */
    public ConsoleReader getConsoleReader();

    /**
     * The Logger is used to perform information's and so on to the log.
     */
    public CloudLogger getLogger();

    /**
     * The ServerHandler manages server groups and permanently servers.
     * Server groups manage servers like game-servers or lobby-servers.
     * Permanent-servers are good for survival servers, build servers or something like this.
     */
    public ServerHandler getServerHandler();

    /**
     * The Bungee manages the bungee instance from the cloud.
     * It's only used to start and stop the bungee instance.
     * This instance can be managed in the Bungee folder like a normal bungee server.
     */
    public Bungee getBungee();

    /**
     * The DatabaseManager is used to manage all database things.
     * Here you can find all mongodb data's.
     */
    public DatabaseManager getDatabaseManager();

    /**
     * The DatabaseServer put's all data's from servers in it and load this data any time.
     * You can get information's like player count and
     * server id from the cloudAPI in the bungee or spigot plugin.
     */
    public DatabaseServer getDatabaseServer();

    /**
     * This returns the correct version from the cloud.
     * It contains information's about git commit and jenkins build number.
     * If you doesn't build the software with jenkins you would get and "unknown" version.
     */
    public String getVersion();

    /**
     * This handler is used to register commands for the console input.
     */
    public CommandHandler getCommandHandler();

    /**
     * This string returns the cloud start date with time.
     */
    public String getCloudStarted();

    /**
     * The CloudServer manages all incoming connections.
     * It's the packet server.
     * You get an information in the console when a connection comes in.
     * If this connection comes from a game server or from a bungee
     * you will see this and get informed about this.
     */
    public CloudServer getCloudServer();

    /**
     * The cloudExecutor executes all runnable's.
     */
    public CloudExecutor getCloudExecutor();

    /**
     * This returns the max memory value.
     * It can be changed in the config or with the system priority -Dde.bytelist.bytecloud.maxMem=*memoryInMB*
     */
    public int getMaxMemory();

    /**
     * This returns the screen system.
     */
    public Screen getScreenSystem();

    /**
     * The serverIdOnConnect sets the server to connect on join.
     */
    public String getServerIdOnConnect();

    /**
     * Start all servers, the bungee and the packet server.
     *
     * A {{@link ByteCloud}} instance is required.
     */
    public void start();

    /**
     * This method stops the cloud system with all servers and so on.
     */
    public void stop();

    /**
     * Start the thread to stop the cloud instance automatically.
     */
    public void startAutoStopThread();

    /**
     * This returns the used memory fro mthe cloud system.
     *
     * @return used memory
     */
    public int getUsedMemory();

    /**
     * Returns the configuration.
     */
    public CloudConfig getConfig();
    /**
     * Returns the configuration file.
     */
    public File getConfigFile();
}
