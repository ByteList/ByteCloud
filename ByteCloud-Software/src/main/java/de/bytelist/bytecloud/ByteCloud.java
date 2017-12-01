package de.bytelist.bytecloud;

import de.bytelist.bytecloud.bungee.Bungee;
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
import de.bytelist.bytecloud.server.ServerHandler;
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
import java.util.logging.Logger;

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
    private Logger logger;
    /**
     * The CloudProperties are used to save data's for mongodb and communication systems.
     * In the future maybe replaced by a json format.
     */
    @Getter
    private CloudProperties cloudProperties;
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
     * This string represent a time who the cloud should be stopped.
     * Set this to -1 to disable it.
     * In the future maybe renamed to "stopDate".
     */
    private String restartDate;

    /**
     * Initialise the cloud instance. This doesn't start anything!
     *
     */
    public ByteCloud() {
        instance = this;
        isRunning = false;
        cloudStarted = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        restartDate = System.getProperty("de.bytelist.bytecloud.restart", "03:55");

        // 2.0-23:00342580cc947e7bf8d1eeb7fb8650ab456dc3e2
        String[] v = ByteCloud.class.getPackage().getImplementationVersion().split(":");
        // 2.0-23:0034258
        version = v[0]+":"+v[1].substring(0, 7);

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
            consoleReader = new ConsoleReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
        consoleReader.setExpandEvents(false);

        logger = new CloudLogger("ByteCloud", consoleReader);
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


//        Updater updater = new Updater(Integer.parseInt(v[0].split("-")[1]));
//        if(!updater.isUpdated()) {
//            this.logger.info("================================");
//            this.logger.info("Cloud isn't up-to-date! Try to update it...");
//
//            Thread downloadThread = new Thread("Downloading Thread") {
//                @Override
//                public void run() {
//                        updater.downloadFiles();
//                        updater.extractFiles();
//                }
//            };
//            downloadThread.start();
//
//            int i = 0;
//            while (!updater.isFinished && downloadThread.isAlive()) {
//                i++;
//            }
//
//            if(updater.isSuccessful) {
//                this.logger.info("Update was successful! (t=" + i + ")");
//                this.logger.info("================================");
//                cleanStop();
//            } else {
//                this.logger.warning("Update was not successful! (t=" + i + ")");
//                this.logger.info("Creating directories manually...");
//                for(EnumFile enumFile : EnumFile.values()) {
//                    File file = new File(enumFile.getPath());
//                    if(!file.exists())
//                        file.mkdirs();
//                }
//                this.logger.info("Directories manually created.");
//            }
//        }
        for (EnumFile enumFile : EnumFile.values()) {
            File file = new File(enumFile.getPath());
            if (!file.exists())
                file.mkdirs();
        }
        this.cloudProperties = new CloudProperties();

        this.commandHandler = new CommandHandler();

        Command[] commands = {
                new HelpCommand(),
                new TemplateCommand(),
                new PermanentServerCommand(),
                new ServerCommand(),
                new BungeeCommand(),
                new EndCommand()
        };
        for(Command command : commands) {
            this.commandHandler.registerCommand(command);
        }

        this.bungee = new Bungee();
        this.serverHandler = new ServerHandler();

        String host = this.cloudProperties.getProperty("mongo-host");
        String database = this.cloudProperties.getProperty("mongo-database");
        String user = this.cloudProperties.getProperty("mongo-user");
        String password = this.cloudProperties.getProperty("mongo-password");

        try {
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
        }

        NetworkManager.connect(Integer.valueOf(cloudProperties.getProperty("socket-port", "4213")), this.logger);
        this.cloudServer = new CloudServer();
    }

    /**
     * Start all servers, the bungee and the packet server.
     *
     * A {{@link ByteCloud}} instance is required.
     */
    public void start() {
        isRunning = true;

        this.cloudServer.startPacketServer();

        this.bungee.startBungee();
        try {
            Thread.sleep(7000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.serverHandler.start();
    }

    /**
     * This method stops the cloud system with all servers and so on.
     */
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
        System.exit( 0 );
    }

    public void startRestartThread() {
        if(restartDate.equals("false")) {
            this.logger.info("Restart is disabled.");
            return;
        }

        new Thread("Cloud Restart Thread") {

            @Override
            public void run() {
                ByteCloud.this.logger.info("Restart will be executed at "+restartDate+".");

                while (ByteCloud.this.isRunning) {
                    try {
                        Thread.sleep(60000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String date = new SimpleDateFormat("HH:mm").format(new Date());

                    if(date.equals(restartDate)) {
                        ByteCloud.this.logger.info("** Automatic Restart executed at "+restartDate+" **");
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ByteCloud.this.stop();
                    }
                }
            }
        }.start();
    }
}