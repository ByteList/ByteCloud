package de.bytelist.bytecloud.log;

import de.bytelist.bytecloud.file.EnumFile;
import jline.console.ConsoleReader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
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

    private final LogDispatcher dispatcher;
    private final String date;

    public CloudLogger(String name, ConsoleReader reader) {
        super(name, null);
        setLevel(Level.ALL);

        this.dispatcher = new LogDispatcher(this);
        this.date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        try {
            File file = new File(EnumFile.CLOUD_LOGS.getPath());
            if (!file.exists()) file.mkdirs();
            String logFileName = EnumFile.CLOUD_LOGS.getPath() + date + "_" +
                    (file.exists() ? Objects.requireNonNull(file.listFiles(file1 -> file1.getName().startsWith(date))).length : 0) + ".log";


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

    @Override
    public void log(LogRecord record) {
        dispatcher.queue(record);
    }

    void doLog(LogRecord record) {
        super.log(record);
    }
}
