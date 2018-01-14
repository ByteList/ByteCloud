package de.bytelist.bytecloud.log;

import lombok.Getter;

import java.util.logging.LogRecord;

/**
 * Created by ByteList on 14.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class DispatcherElement {

    @Getter
    private LogRecord logRecord;
    @Getter
    private final boolean fromScreen;

    public DispatcherElement(LogRecord logRecord, boolean fromScreen) {
        this.logRecord = logRecord;
        this.fromScreen = fromScreen;
    }
}
