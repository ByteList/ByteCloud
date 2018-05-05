package de.bytelist.bytecloud.log;

import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by ByteList on 02.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class LogWriter extends Handler {

    private final ConsoleReader console;

    LogWriter(ConsoleReader console) {
        this.console = console;
    }

    private void print(String s) {
        try {
            console.print(Ansi.ansi().eraseLine(Ansi.Erase.ALL).toString() + ConsoleReader.RESET_LINE + s + Ansi.ansi().reset().toString());
            console.drawLine();
            console.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            print(getFormatter().format(record));
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }
}
