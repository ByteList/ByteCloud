package de.bytelist.bytecloud.server.screen;

/**
 * Created by ByteList on 10.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface IScreen {

    public void runCommand(String command);

    public String getServerId();

    public Process getProcess();
}
