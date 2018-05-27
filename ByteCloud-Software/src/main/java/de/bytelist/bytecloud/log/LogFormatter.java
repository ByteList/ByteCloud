package de.bytelist.bytecloud.log;

import de.bytelist.bytecloud.ByteCloud;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Created by ByteList on 27.01.2017.
 *
 * Copyright by ByteList - https://bytelist.de/
 */
public class LogFormatter extends Formatter {

    private final DateFormat date = new SimpleDateFormat("HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        if(ByteCloud.getInstance().getScreenSystem().getScreen() == null) {
            return formatted(record, Mode.NORMAL);
        } else {
            if(record.getMessage().startsWith("#%§DEbuG§#%")) {
                return formatted(record, Mode.DEBUG);
            } else if(record.getMessage().startsWith("#%scr3En%#")) {
                return formatted(record, Mode.SCREEN);
            } else {
                return formatted(record, Mode.NORMAL);
            }
        }
    }

    private String formatted(LogRecord record, Mode mode) {
        StringBuilder formatted = new StringBuilder();

        formatted.append(" ");

        switch (mode) {
            case DEBUG:
                formatted.append(AnsiColor.GREEN);
                formatted.append(this.date.format(record.getMillis()));
                formatted.append(AnsiColor.GRAY);
                formatted.append(" | ");
                formatted.append(AnsiColor.RED);
                formatted.append("DEBUG");
                break;
            case SCREEN:
                formatted.append(AnsiColor.RED);
                formatted.append(ByteCloud.getInstance().getScreenSystem().getScreen().getServerId());
                break;
            case NORMAL:
                formatted.append(AnsiColor.GREEN);
                formatted.append(this.date.format(record.getMillis()));
                formatted.append(AnsiColor.GRAY);
                formatted.append(" | ");
                if(record.getLevel().getName().startsWith("INFO")) {
                    formatted.append(AnsiColor.YELLOW);
                } else {
                    formatted.append(AnsiColor.RED);
                }
                formatted.append(record.getLevel().getName());
                break;
        }
        formatted.append(AnsiColor.GRAY);
        formatted.append(" | ");
        formatted.append(AnsiColor.WHITE);
        formatted.append(formatMessage(record).replaceFirst("#%scr3En%#", "").replaceFirst("#%§DEbuG§#%", ""));
        formatted.append(AnsiColor.DEFAULT);
        formatted.append('\n');

        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            formatted.append(writer);
        }
        return formatted.toString();
    }

    private enum Mode {
        DEBUG, SCREEN, NORMAL
    }
}
