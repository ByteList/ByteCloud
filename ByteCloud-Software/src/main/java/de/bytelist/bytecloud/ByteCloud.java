package de.bytelist.bytecloud;

import de.bytelist.bytecloud.bungee.Bungee;
import de.bytelist.bytecloud.config.CloudConfig;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.console.CommandHandler;
import de.bytelist.bytecloud.console.commands.*;
import de.bytelist.bytecloud.database.DatabaseManager;
import de.bytelist.bytecloud.database.DatabaseServer;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.log.AnsiColor;
import de.bytelist.bytecloud.log.CloudLogger;
import de.bytelist.bytecloud.log.LoggingOutPutStream;
import de.bytelist.bytecloud.network.NetworkManager;
import de.bytelist.bytecloud.network.cloud.CloudServer;
import de.bytelist.bytecloud.server.Server;
import de.bytelist.bytecloud.server.ServerHandler;
import de.bytelist.bytecloud.server.screen.Screen;
import de.bytelist.bytecloud.updater.Updater;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.AnsiConsole;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Handler;
import java.util.logging.Level;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloud {

    /**
     * This boolean returns the running status from the cloud instance.
     */
    public boolean isRunning;

    /**
     * That's the cloud instance. It's needed to return the main class.
     */
    @Getter
    private static ByteCloud instance;
    /**
     * The ConsoleReader is used to optimize the console out- and input.
     */
    @Getter
    private ConsoleReader consoleReader;
    /**
     * The Logger is used to perform information's and so on to the log.
     */
    @Getter
    private CloudLogger logger;
    /**
     * The ServerHandler manages server groups and permanently servers.
     * Server groups manage servers like game-servers or lobby-servers.
     * Permanent-servers are good for survival servers, build servers or something like this.
     */
    @Getter
    private ServerHandler serverHandler;
    /**
     * The Bungee manages the bungee instance from the cloud.
     * It's only used to start and stop the bungee instance.
     * This instance can be managed in the Bungee folder like a normal bungee server.
     */
    @Getter
    private Bungee bungee;
    /**
     * The DatabaseManager is used to manage all database things.
     * Here you can find all mongodb data's.
     */
    @Getter
    private DatabaseManager databaseManager;
    /**
     * The DatabaseServer put's all data's from servers in it and load this data any time.
     * You can get information's like player count and
     * server id from the cloudAPI in the bungee or spigot plugin.
     */
    @Getter
    private DatabaseServer databaseServer;
    /**
     * This returns the correct version from the cloud.
     * It contains information's about git commit and jenkins build number.
     * If you doesn't build the software with jenkins you would get and "unknown" version.
     */
    @Getter
    private final String version;
    /**
     * This handler is used to register commands for the console input.
     */
    @Getter
    private CommandHandler commandHandler;
    /**
     * This string returns the cloud start date with time.
     */
    @Getter
    private final String cloudStarted;
    /**
     * The CloudServer manages all incoming connections.
     * It's the packet server.
     * You get an information in the console when a connection comes in.
     * If this connection comes from a game server or from a bungee
     * you will see this and get informed about this.
     */
    @Getter
    private CloudServer cloudServer;

    /**
     * This string represents a time who the cloud should be stopped.
     * Set this to false to disable it.
     * <p>
     * Arg: -Dde.bytelist.bytecloud.stop=03:55
     */
    private String stopDate;

    /**
     * This is used to start an Fallback server if cloud get stopped.
     * It will run at the minecraft standard port (25565).
     * You can edit this server in Fallback-Server/
     * Set this to false to disable it.
     * <p>
     * Arg: -Dde.bytelist.bytecloud.startFallback=false
     */
    private String startFallback;
    /**
     * The cloudExecutor executes all runnable's.
     */
    @Getter
    private CloudExecutor cloudExecutor;
    /**
     * This returns the max memory value.
     * It can be changed in the config or with the system priority -Dde.bytelist.bytecloud.maxMem=*memoryInMB*
     */
    @Getter
    private int maxMemory;
    /**
     * This returns the screen system.
     */
    @Getter
    private Screen screenSystem;
    /**
     * The serverIdOnConnect sets the server to connect on join.
     */
    @Getter
    private String serverIdOnConnect;
    /**
     * Returns the configuration.
     */
    @Getter
    private CloudConfig cloudConfig;
    /**
     * Returns the configuration file.
     */
    @Getter
    private File configFile;

    /**
     * Initialise the cloud instance. This doesn't start anything!
     *
     */
    public ByteCloud() {
        instance = this;
        isRunning = false;
        cloudStarted = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        stopDate = System.getProperty("de.bytelist.bytecloud.stop", "03:55");
        startFallback = System.getProperty("de.bytelist.bytecloud.startFallback", "true");
        serverIdOnConnect = System.getProperty("de.bytelist.bytecloud.connectServer", "-1");

        cloudExecutor = new CloudExecutor();

        // 2.0.23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = ByteCloud.class.getPackage().getImplementationVersion().split(":");
        // 2.0.23:0034258
        version = v[0]+":"+v[1].substring(0, 7);

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

            this.logger = new CloudLogger("ByteCloud", consoleReader);
            System.setErr(new PrintStream(new LoggingOutPutStream(logger, Level.SEVERE), true));
            System.setOut(new PrintStream(new LoggingOutPutStream(logger, Level.INFO), true));

            System.out.println("Starting cloud system."+
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
        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("Loading config.json...");
        this.configFile = new File(EnumFile.CLOUD.getPath(), "config.json");
        if(!this.configFile.exists()) {
            logger.info("Can not find config.json! Creating one...");
            CloudConfig cfg = new CloudConfig()
                    .append("update-channel", "stable")
                    .append("mongo-host", "host")
                    .append("mongo-user", "user")
                    .append("mongo-password", "password")
                    .append("mongo-database", "database")
                    .append("web-port", "8090")
                    .append("jar-name", "paperclip")
                    .append("socket-port", "4213")
                    .append("max-memory", "13795")
                    .append("version", this.version)
                    .append("version-type", (isCurrentDevBuild() ? "dev" : "stable"))
                    .append("last-version", "-")
                    .append("last-version-type", "-")
                    .append("last-version-stable", (isCurrentDevBuild() ? "-" : this.version));
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
            this.cloudConfig = CloudConfig.loadDocument(this.configFile);
            this.cloudConfig.append("version", this.version);
            if(!isCurrentDevBuild()) this.cloudConfig.append("last-version-stable", this.version);

            this.cloudConfig.saveAsConfig(this.configFile);
            logger.info("Config loaded.");
        }

        if(System.getProperty("update", "true").equals("true")) {
            new Updater();
        }

        try {
            String host = this.cloudConfig.getString("mongo-host");
            String database = this.cloudConfig.getString("mongo-database");
            String user = this.cloudConfig.getString("mongo-user");
            String password = this.cloudConfig.getString("mongo-password");

            this.databaseManager = new DatabaseManager(host, 27017, user, password, database);
            this.databaseServer = this.databaseManager.getDatabaseServer();
            if(this.databaseServer.getServer().size() > 0) {
                for(String server : this.databaseServer.getServer()) {
                    this.databaseServer.removeServer(server);
                }
            }
            this.logger.info("Connected to database.");
        } catch (Exception e) {
            e.printStackTrace();
            cleanStop();
            return;
        }

        NetworkManager.connect(this.cloudConfig.getInt("socket-port"), this.logger);
        this.cloudServer = new CloudServer();
        if(!this.cloudServer.startPacketServer()) {
            cleanStop();
            return;
        }

        try {
            maxMemory = Integer.parseInt(System.getProperty("de.bytelist.bytecloud.maxMem", this.cloudConfig.getString("max-memory")));
            logger.info("Max memory size is set to: "+maxMemory);
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
                new ScreenCommand()
        };
        for(Command command : commands) {
            this.commandHandler.registerCommand(command);
        }

        this.bungee = new Bungee();
        this.serverHandler = new ServerHandler();
        this.screenSystem = new Screen();
    }

    public void start() {
        isRunning = true;

        this.cloudExecutor.start();
        this.bungee.startBungee();
        try {
            Thread.sleep(7000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.serverHandler.start();
    }

    public void stop() {
        this.isRunning = false;
        new Thread("Shutdown Thread") {

            @Override
            public void run() {
                ByteCloud.this.logger.info("Shutting down...");

                serverHandler.stop();
                bungee.stopBungee();
                while (true) {
                    if(!bungee.isRunning()) break;
                }

                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                cloudServer.getPacketServer().stop();
                cleanStop();
            }
        }.start();
    }

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
                        ByteCloud.this.stop();
                    }
                }
            }
        }.start();
    }

    public int getUsedMemory() {
        int mem = 128;
        for (Server server : serverHandler.getServers()) {
            mem = mem + server.getRamM();
        }

        mem = mem + bungee.getRamM();
        return mem;
    }

    public boolean isCurrentDevBuild() {
        String[] ver = this.version.replace(".", ":").split(":");

        // 2:1:{buildNumber}:dev:{commit}
        return ver.length == 5 && ver[3].equals("dev");
    }
}
