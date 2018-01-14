package de.bytelist.bytecloud.log;

import de.bytelist.bytecloud.file.EnumFile;
import jline.console.ConsoleReader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by ByteList on 27.01.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudLogger extends Logger {

    private final LogDispatcher dispatcher = new LogDispatcher(this);

    public CloudLogger(String name, ConsoleReader reader) {
        super(name, null);
        setLevel(Level.ALL);

        try {
            File file = new File(EnumFile.CLOUD_LOGS.getPath());
            if (!file.exists()) file.mkdirs();
            String logFileName = EnumFile.CLOUD_LOGS.getPath()
                    + new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime()) + ":"
                    + (file.exists() ? file.list().length : 0) + ".log";

            FileHandler fileHandler = new FileHandler(logFileName);
            fileHandler.setFormatter(new FileFormatter());
            addHandler(fileHandler);

            LogWriter consoleHandler = new LogWriter(reader);
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new LogFormatter());
            addHandler(consoleHandler);

        } catch (IOException ex) {
            System.err.println("FileLogging failed.");
            ex.printStackTrace();
        }

        dispatcher.start();
    }

    public void screen(String s) {
        dispatcher.queue(new LogRecord(Level.FINE, s), true);
    }

    @Override
    public void log(LogRecord record) {
        dispatcher.queue(record, false);
    }

    void doLog(LogRecord record) {
        super.log(record);
    }

    void doScreen(LogRecord record) {
        super.log(record);
    }
}
