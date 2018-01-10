package de.bytelist.bytecloud.bootstrap;

import de.bytelist.bytecloud.ByteCloud;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudLauncher {

    public static void main(String[] args) throws Exception {

        ByteCloud byteCloud = new ByteCloud();
        byteCloud.getLogger().info("Enabled ByteCloud version " + byteCloud.getVersion() + ".");
        byteCloud.start();
        byteCloud.startRestartThread();

        String input;
        while (byteCloud.isRunning && (input = byteCloud.getConsoleReader().readLine(">")) != null) {
            if (!byteCloud.getCommandHandler().dispatchCommand(input))
                if (byteCloud.getScreenSystem().getScreen() != null) {
                    byteCloud.getScreenSystem().getScreen().runCommand(input);
                } else {
                    byteCloud.getLogger().info("** Command not found");
                }
        }
    }
}
