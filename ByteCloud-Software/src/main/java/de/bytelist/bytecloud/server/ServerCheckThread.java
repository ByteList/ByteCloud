package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.ByteCloud;

/**
 * Created by ByteList on 01.05.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerCheckThread extends Thread {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public ServerCheckThread() {
        super("Server Check Thread");
    }

    @Override
    public void run() {
        while (byteCloud.isRunning) {
            for (ServerGroup serverGroup : byteCloud.getServerHandler().getServerGroups().values()) {
                serverGroup.checkAndStartNewServer();
            }

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
