package de.bytelist.bytecloud.task;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.server.ServerGroup;

/**
 * Created by ByteList on 01.05.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerHandleTask implements Runnable {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

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
