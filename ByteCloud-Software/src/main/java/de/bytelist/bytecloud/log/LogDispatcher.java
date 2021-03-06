package de.bytelist.bytecloud.log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;

/**
 * Created by ByteList on 02.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class LogDispatcher extends Thread {


    private final CloudLogger logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    LogDispatcher(CloudLogger logger) {
        super("Cloud Logger Thread");
        this.logger = logger;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            LogRecord record;
            try {
                record = queue.take();
            } catch (InterruptedException ex) {
                continue;
            }

            logger.doLog(record);
        }
        for (LogRecord record : queue) {
            logger.doLog(record);
        }
    }

    void queue(LogRecord record) {
        if (!isInterrupted() && !record.getMessage().equals("#%i$Sc3en%#")) {
            queue.add(record);
        }
    }
}
