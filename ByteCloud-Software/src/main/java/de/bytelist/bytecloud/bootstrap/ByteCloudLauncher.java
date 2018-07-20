package de.bytelist.bytecloud.bootstrap;

import de.bytelist.bytecloud.ByteCloud;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ByteCloudLauncher {

    /**
     * Initialise and start the cloud.
     * Checks and executes the console input.
     * @param args given arguments
     * @throws Exception all unhandled exceptions
     */
    public static void main(String[] args) throws Exception {

        ByteCloud byteCloud = new ByteCloud();
        byteCloud.getLogger().info("Enabled ByteCloud version " + byteCloud.getVersion() + ".");
        byteCloud.start();
        byteCloud.startAutoStopThread();

        String input;
        while (byteCloud.isRunning && (input = byteCloud.getConsoleReader().readLine(">")) != null) {
            if(!input.startsWith("screen") && byteCloud.getScreenManager().getScreen() != null) {
                byteCloud.getScreenManager().getScreen().runCommand(input);
            } else {
                if (!byteCloud.getCommandHandler().dispatchCommand(input))
                    byteCloud.getLogger().info("** Command not found");
            }
        }
    }
}
