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
    private final BlockingQueue<DispatcherElement> queue = new LinkedBlockingQueue<>();

    public LogDispatcher(CloudLogger logger) {
        super("Cloud Logger Thread");
        this.logger = logger;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            LogRecord record;
            try {
                DispatcherElement dispatcherElement = queue.take();
                record = dispatcherElement.getLogRecord();
                if (dispatcherElement.isFromScreen()) {
                    logger.doScreen(record);
                } else {
                    logger.doLog(record);
                }
            } catch (InterruptedException ignored) { }

        }
        for (DispatcherElement dispatcherElement : queue) {
            logger.doLog(dispatcherElement.getLogRecord());
        }
    }

    public void queue(LogRecord record, boolean fromScreen) {
        if (!isInterrupted()) {
            queue.add(new DispatcherElement(record, fromScreen));
        }
    }
}
