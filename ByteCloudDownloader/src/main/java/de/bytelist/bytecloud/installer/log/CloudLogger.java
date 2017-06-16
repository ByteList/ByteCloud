package de.bytelist.bytecloud.installer.log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ByteList on 27.01.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudLogger extends Logger {

    private static Logger $;

    public CloudLogger(String name) {

        super(name, null);
        setLevel(Level.ALL);

        try {
            LogFormatter logFormatter = new LogFormatter();

            FileHandler fileHandler = new FileHandler("bytecloud-downloader.log");
            fileHandler.setFormatter(logFormatter);
            addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(logFormatter);
            consoleHandler.setLevel(Level.INFO);
            addHandler(consoleHandler);

            $(this);

            System.out.println("[CloudLogger] Loaded!");
        } catch (IOException ex) {
            System.err.println("[CloudLogger] FileLogging failed.");
            ex.printStackTrace();
        }
    }

    public static Logger $() {
        return $;
    }

    private static void $(Logger logger) {
        CloudLogger.$ = logger;
    }
}
