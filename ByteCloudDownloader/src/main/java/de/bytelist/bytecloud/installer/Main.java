package de.bytelist.bytecloud.installer;

import de.bytelist.bytecloud.installer.log.CloudLogger;
import de.bytelist.bytecloud.installer.log.LoggingOutPutStream;
import jline.console.ConsoleReader;
import lombok.Getter;
import org.fusesource.jansi.AnsiConsole;

import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ByteList on 31.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Main {

    public static boolean isRunning;
    @Getter
    private static ConsoleReader consoleReader;
    @Getter
    private static Logger logger;


    public static void main(String[] args) throws IOException {
        System.out.println("Setup downloader...");
        isRunning = false;
        System.setProperty("library.jansi.version", "ByteCloud-Downloader");

        AnsiConsole.systemInstall();

        consoleReader = new ConsoleReader();
        consoleReader.setExpandEvents(false);

        logger = new CloudLogger("ByteCloud-Downloader");
        System.setErr(new PrintStream(new LoggingOutPutStream(logger, Level.SEVERE), true));
        System.setOut(new PrintStream(new LoggingOutPutStream(logger, Level.INFO), true));

        isRunning = true;

        String line;
        while (isRunning && ( line = consoleReader.readLine( ">" ) ) != null) {
            if(line.startsWith("download")) {
                Downloader downloader = new Downloader();
                    System.out.println("================================");
                    logger.info("Cloud isn't up-to-date! Try to update it...");

                    Thread downloadThread = new Thread("Downloading Thread") {
                        @Override
                        public void run() {
                            downloader.downloadFiles();
                            downloader.extractFiles();
                        }
                    };
                    downloadThread.start();

                    int i = 0;
                    while (!downloader.isFinished && downloadThread.isAlive()) {
                        i++;
                    }
                    if(downloader.isSuccessful) {
                        logger.info("Update was successful! (t=" + i + ")");
                    }
                    System.out.println("================================");
                    logger.info("Continue cloud initialization...");
            }
            else
            if(line.startsWith("end")) {
                end();
            } else {
                System.out.println("Unknown command! Use 'end' to shutdown.");
            }
        }
    }

    public static void end() {
        if(isRunning) {
            new Thread("Shutdown Thread") {

                @Override
                public void run() {
                    isRunning = false;

                    for (Handler handler : getLogger().getHandlers()) {
                        handler.close();
                    }
                    System.exit(0);
                }
            }.start();
        }
    }
}
