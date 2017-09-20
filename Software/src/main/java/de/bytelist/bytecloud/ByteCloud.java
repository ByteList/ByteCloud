package de.bytelist.bytecloud;

import de.bytelist.bytecloud.bungee.Bungee;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.console.CommandHandler;
import de.bytelist.bytecloud.console.commands.*;
import de.bytelist.bytecloud.database.DatabaseManager;
import de.bytelist.bytecloud.database.DatabaseServer;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.installer.Installer;
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

    public boolean isRunning;
    @Getter
    private static ByteCloud instance;
    @Getter
    private ConsoleReader consoleReader;
    @Getter
    private Logger logger;
    @Getter
    private CloudProperties cloudProperties;
    @Getter
    private ServerHandler serverHandler;
    @Getter
    private Bungee bungee;
    @Getter
    private DatabaseManager databaseManager;
    @Getter
    private DatabaseServer databaseServer;
    @Getter
    private final String version;
    @Getter
    private CommandHandler commandHandler;
    @Getter
    private final String cloudStarted;
    @Getter
    private CloudServer cloudServer;

    private String restartDate;

    public ByteCloud() throws Exception {
        instance = this;
        isRunning = false;
        cloudStarted = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date());
        restartDate = System.getProperty("de.bytelist.bytecloud.restart", "03:55");
        version = ByteCloud.class.getPackage().getImplementationVersion();

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
        consoleReader = new ConsoleReader();
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


        Installer installer = new Installer();
        if(!installer.isUpdated()) {
            this.logger.info("================================");
            this.logger.info("Cloud isn't up-to-date! Try to update it...");

            Thread downloadThread = new Thread("Downloading Thread") {
                @Override
                public void run() {
                        installer.downloadFiles();
                        installer.extractFiles();
                }
            };
            downloadThread.start();

            int i = 0;
            while (!installer.isFinished && downloadThread.isAlive()) {
                i++;
            }

            if(installer.isSuccessful) {
                this.logger.info("Update was successful! (t=" + i + ")");
                this.logger.info("================================");
                cleanStop();
            } else {
                this.logger.warning("Update was not successful! (t=" + i + ")");
                this.logger.info("Creating directories manually...");
                for(EnumFile enumFile : EnumFile.values()) {
                    File file = new File(enumFile.getPath());
                    if(!file.exists())
                        file.mkdirs();
                }
                this.logger.info("Directories manually created.");
            }
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