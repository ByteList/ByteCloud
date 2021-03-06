package de.bytelist.bytecloud;

import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.sun.management.OperatingSystemMXBean;
import de.bytelist.bytecloud.bungee.Bungee;
import de.bytelist.bytecloud.common.*;
import de.bytelist.bytecloud.common.packet.cloud.player.*;
import de.bytelist.bytecloud.config.CloudConfig;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.console.CommandHandler;
import de.bytelist.bytecloud.console.commands.*;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.log.AnsiColor;
import de.bytelist.bytecloud.log.CloudLogger;
import de.bytelist.bytecloud.log.LoggingOutPutStream;
import de.bytelist.bytecloud.packet.ByteCloudPacketCloudListener;
import de.bytelist.bytecloud.packet.ByteCloudPacketProtocol;
import de.bytelist.bytecloud.restapi.WebService;
import de.bytelist.bytecloud.restapi.WebSocket;
import de.bytelist.bytecloud.server.PermServer;
import de.bytelist.bytecloud.server.Server;
import de.bytelist.bytecloud.server.ServerHandler;
import de.bytelist.bytecloud.server.screen.ScreenManager;
import de.bytelist.bytecloud.updater.UpdateChannel;
import de.bytelist.bytecloud.updater.Updater;
import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.Setter;
import org.fusesource.jansi.AnsiConsole;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloud implements CloudSoftware.ICloudSoftware {

    /**
     * This boolean returns the running status from the cloud instance.
     */
    public boolean isRunning;

    /**
     * Gets the current {@link ByteCloud} singleton.
     */
    @Getter
    private static ByteCloud instance;
    /**
     * The ConsoleReader is used to optimize the console out- and input.
     */
    @Getter
    private ConsoleReader consoleReader;
    /**
     * The {@link CloudLogger} is used to perform information's and so on to the log.
     */
    @Getter
    private CloudLogger logger;
    /**
     * The {@link ServerHandler} manages cloud groups and permanently servers.
     * <p>
     * A {@link de.bytelist.bytecloud.server.ServerGroup} manages servers like game or lobby servers. These are temporary servers.
     * A {@link de.bytelist.bytecloud.server.PermServer} is good for a survival cloud or build cloud. It's a static cloud.
     */
    @Getter
    private ServerHandler serverHandler;
    /**
     * The {@link Bungee} manages the bungee instance from the cloud.
     * <p>
     * It's only used to start and stop the bungee instance.
     * This instance can be managed in the Bungee folder like a normal bungee cloud.
     */
    @Getter
    private Bungee bungee;
    /**
     * This returns the correct version from the cloud.
     * It contains information's about git commit and jenkins build number.
     * If you doesn't build the software with jenkins you will get the maven version.
     */
    @Getter
    private final String version;
    /**
     * The {@link CommandHandler} is used to register commands for the console input.
     */
    @Getter
    private CommandHandler commandHandler;
    /**
     * This string returns the cloud start date with time.
     */
    @Getter
    private final String cloudStarted;
    /**
     * The {@link com.github.steveice10.packetlib.Server} manages all incoming connections.
     * It's the packet cloud.
     * You get an information in the console when a connection comes in.
     * If this connection comes from a game cloud or from a bungee
     * you will see this and get informed about this.
     */
    @Getter
    private com.github.steveice10.packetlib.Server packetServer;

    /**
     * This string represents a time who the cloud should be stopped.
     * Set this to false to disable it.
     * <p>
     * Arg: -Dde.bytelist.bytecloud.stop=03:55
     */
    private String stopDate;

    /**
     * This is used to start an Fallback cloud if cloud get stopped.
     * It will run at the minecraft standard port (25565).
     * You can edit this cloud in Fallback-Server/
     * Set this to false to disable it.
     * <p>
     * Arg: -Dde.bytelist.bytecloud.startFallback=false
     */
    private String startFallback;
    /**
     * The {@link CloudExecutor} executes all runnable's.
     */
    @Getter
    private CloudExecutor cloudExecutor;
    /**
     * This returns the max memory value.
     * It can be changed in the config or with the system property <code>de.bytelist.bytecloud.maxMem</code>
     * The unit is mb.
     */
    @Getter
    private int maxMemory;
    /**
     * This returns the {@link ScreenManager}.
     * It's a manager to output the process log from a {@link de.bytelist.bytecloud.server.screen.IScreen} to the console.
     */
    @Getter
    private ScreenManager screenManager;
    /**
     * Sets the force cloud to connect on join.
     * This can be set in the system property <code>de.bytelist.bytecloud.connectServer</code>
     */
    @Getter
    private String serverIdOnConnect;
    /**
     * Returns the current {@link CloudConfig}.
     */
    @Getter
    private CloudConfig cloudConfig;
    /**
     * Returns the configuration file.
     */
    @Getter
    private File configFile;

    /**
     * If enabled you get more information about some tasks or somthing like this.
     */
    @Getter @Setter
    private boolean debug;
    @Getter
    private WebService webService;
    @Getter
    private WebSocket webSocket;

    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    @Getter
    private String packetEncryptionKey;

    @Getter
    private HashMap<UUID, CloudPlayer> cloudPlayers = new HashMap<>();
    private HashMap<UUID, Server> currentServer = new HashMap<>();

    @Getter
    private final boolean localMode;

    /**
     * Initialise the cloud instance. This doesn't start anything!
     *
     */
    public ByteCloud() {
        CloudSoftware.setInstance(instance = this);
        this.isRunning = false;
        this.debug = Boolean.valueOf(System.getProperty("de.bytelist.bytecloud.debug", "false"));
        this.cloudStarted = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        this.stopDate = System.getProperty("de.bytelist.bytecloud.stop", "03:55");
        this.startFallback = System.getProperty("de.bytelist.bytecloud.startFallback", "true");
        this.serverIdOnConnect = System.getProperty("de.bytelist.bytecloud.connectServer", "-1");
        this.localMode = Boolean.parseBoolean(System.getProperty("de.bytelist.local", "false"));

        this.cloudExecutor = new CloudExecutor();
        this.cloudExecutor.setExtendedDebug(Boolean.valueOf(System.getProperty("de.bytelist.bytecloud.debug", "false")));

        this.screenManager = new ScreenManager();


        // 2.0.23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = ByteCloud.class.getPackage().getImplementationVersion().split(":");
        // 2.0.23:0034258
        this.version = v[0]+":"+v[1].substring(0, 7);


        for (EnumFile enumFile : EnumFile.values()) {
            File file = new File(enumFile.getPath());
            if (!file.exists())
                file.mkdirs();
        }

        // This is a workaround for quite possibly the weirdest bug I have ever encountered in my life!
        // When jansi attempts to extract its natives, by default it tries to extract a specific version,
        // using the loading class's implementation version. Normally this works completely fine,
        // however when on Windows certain characters such as - and : can trigger special behaviour.
        // Furthermore this behaviour only occurs in specific combinations due to the parsing done by jansi.
        // For example test-test works fine, but test-test-test does not! In order to avoid this all together but
        // still keep our versions the same as they were, we set the override property to the essentially garbage version
        // ByteCloud. This version is only used when extracting the libraries to their temp folder.
        System.setProperty("library.jansi.version", "ByteCloud");

        AnsiConsole.systemInstall();
        try {
            this.consoleReader = new ConsoleReader();
            this.consoleReader.setExpandEvents(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.logger = new CloudLogger("ByteCloud", this.consoleReader);
        System.setErr(new PrintStream(new LoggingOutPutStream(this.logger, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggingOutPutStream(this.logger, Level.INFO), true));

        logger.info("Starting cloud system."+
                "\n"+ AnsiColor.CYAN +"\n" +
                "   ____        _        _____ _                 _ \n" +
                "  |  _ \\      | |      / ____| |               | |\n" +
                "  | |_) |_   _| |_ ___| |    | | ___  _   _  __| |\n" +
                "  |  _ <| | | | __/ _ \\ |    | |/ _ \\| | | |/ _` |\n" +
                "  | |_) | |_| | ||  __/ |____| | (_) | |_| | (_| |\n" +
                "  |____/ \\__, |\\__\\___|\\_____|_|\\___/ \\__,_|\\__,_|\n" +
                "          __/ | T I G E R\n" +
                "         |___/                 b y   B y t e L i s t\n" +
                "\n\n");

        logger.info("Loading config.json...");
        this.configFile = new File(EnumFile.CLOUD.getPath(), "config.json");
        if(!this.configFile.exists()) {
            logger.info("Can not find config.json! Creating one...");
            CloudConfig cfg = new CloudConfig()
                    .append("version", this.version)
                    .append("version-type", (isCurrentDevBuild() ? "dev" : "stable"))
                    .append("last-version", this.version)
                    .append("last-version-type", (isCurrentDevBuild() ? "dev" : "stable"))
                    .append("last-version-stable", (isCurrentDevBuild() ? "-" : this.version))
                    .append("update-channel", "stable")
                    .append("web-dashboard", "http://127.0.0.1/cloud/")
                    .append("web-auth", "not-generated")
                    .append("web-restapi-port", "49999")
                    .append("web-socket-port", "49998")
                    .append("jar-name", "paperclip")
                    .append("socket-port", "4213")
                    .append("max-memory", "13795");
            cfg.saveAsConfig(this.configFile);
            logger.info("****************");
            logger.info(" ");
            logger.info(" ");
            logger.info("Config created! Please, setup the config.");
            logger.info("Cloud instance will shutdown in 5 seconds.");
            logger.info(" ");
            logger.info(" ");
            logger.info("****************");
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cleanStop();
            return;
        } else {
            this.cloudConfig = CloudConfig.loadDocument(this.configFile)
                    .append("version", this.version)
                    .append("version-type", (isCurrentDevBuild() ? "dev" : "stable"));
            if(!isCurrentDevBuild()) this.cloudConfig.append("last-version-stable", this.version);

            this.cloudConfig.saveAsConfig(this.configFile);
            logger.info("Config loaded.");
        }

        if(localMode) {
            this.logger.warning("***** LOCAL MODE ACTIVATED *****");
        }

        if(System.getProperty("update", "true").equals("true") && !localMode) {
            Updater updater = new Updater(UpdateChannel.getUpdateChannel(this.cloudConfig.getString("update-channel")), true);
            while (true) {
                if(!updater.isAlive()) break;
            }
        }

        SecretKey key = null;
        try {
            KeyGenerator gen = KeyGenerator.getInstance("AES");
            gen.init(128);
            key = gen.generateKey();
        } catch(NoSuchAlgorithmException e) {
            System.err.println("AES algorithm not supported, exiting...");
            cleanStop();
            return;
        }
        this.packetEncryptionKey = Base64.getEncoder().encodeToString(key.getEncoded());

        this.packetServer = new com.github.steveice10.packetlib.Server("127.0.0.1", this.cloudConfig.getInt("socket-port"), ByteCloudPacketProtocol.class, new TcpSessionFactory());
        this.packetServer.addListener(new ByteCloudPacketCloudListener(key));

        try {
            maxMemory = Integer.parseInt(System.getProperty("de.bytelist.bytecloud.maxMem", this.cloudConfig.getString("max-memory")));
            logger.info("Max memory size is set to: "+maxMemory+"MB");
        } catch (NumberFormatException ex) {
            System.err.println("Max memory size must be a number!");
            cleanStop();
            return;
        }

        this.commandHandler = new CommandHandler();

        Command[] commands = {
                new HelpCommand(),
                new TemplateCommand(),
                new PermanentServerCommand(),
                new ServerCommand(),
                new BungeeCommand(),
                new EndCommand(),
                new ScreenCommand(),
                new DebugCommand(),
                new VersionCommand()
        };
        for(Command command : commands) {
            this.commandHandler.registerCommand(command);
        }

        this.webService = new WebService(this.logger, this.cloudConfig.getInt("web-restapi-port"), false);
        this.webSocket = new WebSocket(this.logger, this.cloudConfig.getInt("web-socket-port"), false);

        this.bungee = new Bungee();
        this.serverHandler = new ServerHandler();
    }

    /**
     * Starts {@link CloudExecutor}, {@link Bungee} and {@link ServerHandler}.
     */
    public void start() {
        isRunning = true;

        this.packetServer.bind();
        this.cloudExecutor.start();
        if(!localMode)
            this.webService.startWebServer();

        this.bungee.startBungee(()-> this.serverHandler.start());
    }

    /**
     * Stops all running processes.
     */
    public void stop() {
        this.isRunning = false;
        new Thread("Shutdown Thread") {

            @Override
            public void run() {
                ByteCloud.this.logger.info("Shutting down...");

                Runnable lastStop = ()-> {
                    if(packetServer.isListening())
                        packetServer.close();

                    logger.info("ByteCloud stopped.");
                    cleanStop();
                };

                serverHandler.stop(()-> bungee.stopBungee(lastStop, lastStop), lastStop);
            }
        }.start();
    }

    /**
     * Closes all handlers from {@link CloudLogger#getHandlers()}.
     * Start the fallback cloud if it's set to true and exit the system.
     */
    private void cleanStop() {
        for (Handler handler : getLogger().getHandlers()) {
            handler.close();
        }
        if(startFallback.equals("true")) {
            Thread shutdownHook = new Thread(() -> {
                String[] param = {"sh", System.getProperty("FallbackSh", "start_fallback")+".sh"};
                try {
                    Runtime.getRuntime().exec(param);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            shutdownHook.setDaemon(true);
            Runtime.getRuntime().addShutdownHook(shutdownHook);
        }
        System.exit( 0 );
    }

    /**
     * Starts the auto stop thread.
     * This thread looks every minute for the current time.
     * If the current time equal the stop date the cloud will stop.
     */
    public void startAutoStopThread() {
        if(stopDate.equals("false")) {
            this.logger.info("Automatic Stop is disabled.");
            return;
        }

        new Thread("Cloud Automatic Stop Thread") {

            @Override
            public void run() {
                ByteCloud.this.logger.info("Automatic Stop will be executed at "+stopDate+".");

                while (ByteCloud.this.isRunning) {
                    try {
                        Thread.sleep(60000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String date = new SimpleDateFormat("HH:mm").format(new Date());

                    if(date.equals(stopDate)) {
                        ByteCloud.this.logger.info("** Automatic Stop executed at "+stopDate+" **");
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startFallback = "false";

                        UpdateChannel updateChannel = UpdateChannel.getUpdateChannel(cloudConfig.getString("update-channel"));
                        Updater updater = new Updater(updateChannel, false);
                        while (true) {
                            if(!updater.isAlive()) break;
                        }

                        ByteCloud.this.stop();
                    }
                }
            }
        }.start();
    }

    /**
     * Gets the current max used memory size from all processes.
     * @return the current max used memory size from all processes
     */
    public int getUsedMemory() {
        int mem = 128;
        Collection<Server> servers = serverHandler.getServers();
        for (Server server : servers) {
            mem = mem + server.getRamM();
        }

        mem = mem + bungee.getRamM();
        return mem;
    }

    public double getCurrentSystemCpuLoad() {
        return Math.round(this.osBean.getSystemCpuLoad() * 10000) / 100.0;
    }
    public double getCurrentSystemMemoryLoad() {
        return Math.round((double)this.osBean.getFreePhysicalMemorySize() / (double)this.osBean.getTotalPhysicalMemorySize() * 10000) / 100.0;
    }

    public double getCurrentCloudCpuLoad() {
        return Math.round(this.osBean.getProcessCpuLoad() * 10000) / 100.0;
    }

    public double getCurrentCloudMemoryLoad() {
        return Math.round((double)this.getUsedMemory() / (double)this.maxMemory * 10000) / 100.0;
    }

    public double getStorageLoad() {
        File root = new File("/");
        return Math.round((double)(root.getTotalSpace()-root.getFreeSpace()) / (double)root.getTotalSpace() * 10000) / 100.0;
    }

    /**
     * Gets is the current instance a dev-build from the version string.
     * @return is the current instance a dev-build
     */
    public boolean isCurrentDevBuild() {
        String[] ver = this.version.replace(".", ":").split(":");

        // 2:1:{buildNumber}:dev:{commit}
        return ver.length == 5 && ver[3].equals("dev");
    }

    /**
     * Send a debug formatted message to the cloud logger.
     * @param message the debug message
     */
    public void debug(String message) {
        if(isDebug()) this.logger.warning("#%??DEbuG??#%"+message);
    }

    @Override
    public Executable getExecutable(String serverId) {
        if(this.bungee.getBungeeId().equals(serverId)) {
            return this.bungee;
        }

        return this.serverHandler.getServer(serverId);
    }

    @Override
    public IServer getIServer(String serverId) {
        Executable executable = getExecutable(serverId);
        if(executable instanceof IServer) {
            return (IServer) executable;
        }
        return null;
    }

    @Override
    public void connectPlayer(CloudPlayer cloudPlayer) {
        this.cloudPlayers.put(cloudPlayer.getUuid(), cloudPlayer);
        this.sendGlobalPacket(new CloudPlayerConnectPacket(cloudPlayer.getUuid(), cloudPlayer.getName()));
    }

    @Override
    public void disconnectPlayer(UUID uuid) {
        this.cloudPlayers.remove(uuid);
        this.sendGlobalPacket(new CloudPlayerDisconnectPacket(uuid));
    }

    @Override
    public void kickPlayer(UUID uuid, String reason) {
        this.bungee.getSession().send(new CloudPlayerKickPacket(uuid, reason));
    }

    @Override
    public void setCloudLocation(UUID uuid, CloudLocation cloudLocation) {
        CloudPlayer cloudPlayer = this.cloudPlayers.get(uuid);

        if(cloudPlayer != null) {
            cloudPlayer.setLocation(cloudLocation);
            this.sendGlobalPacket(new CloudPlayerLocationPacket(uuid, cloudLocation));
        }
    }

    @Override
    public void setCurrentServer(UUID uuid, String serverId) {
        CloudPlayer cloudPlayer = this.cloudPlayers.get(uuid);

        if(cloudPlayer != null) {
            Server currentServer = this.currentServer.get(uuid),
                    server = this.getServerHandler().getServer(serverId);

            if(currentServer != null) {
                currentServer.getPlayers().remove(cloudPlayer);
            }
            if(server != null) {
                server.addPlayer(cloudPlayer);
                this.currentServer.put(uuid, server);
            }
        }
    }

    @Override
    public void startTempServer(String group, UUID sender) {
        for (String serverGroupName : this.serverHandler.getServerGroups().keySet()) {
            if(group.equalsIgnoreCase(serverGroupName)) {
                this.serverHandler.getServerGroups().get(serverGroupName).startNewServer(sender.toString());
                return;
            }
        }

        this.bungee.getSession().send(new CloudPlayerMessagePacket(sender, "??cToo much servers are currently online!"));
    }

    @Override
    public void startPermServer(String server, UUID sender) {
        for (PermServer permServer : this.serverHandler.getPermanentServers()) {
            if(permServer.getServerId().equals(server)) {
                permServer.startServer(sender.toString());
                return;
            }
        }

        this.bungee.getSession().send(new CloudPlayerMessagePacket(sender, "??cCan't find a permanent server called "+server+"!"));
    }

    @Override
    public void stopServer(String serverId, UUID sender) {
        Server server = this.serverHandler.getServer(serverId);

        if(server != null) {
            server.stopServer(sender.toString());
            return;
        }

        this.bungee.getSession().send(new CloudPlayerMessagePacket(sender, "??cCan't find a server called "+serverId+"!"));
    }

    public void sendGlobalPacket(Packet packet) {
        this.packetServer.getSessions().forEach(session -> session.send(packet));
    }
}
