package de.bytelist.bytecloud;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ByteList on 22.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudExecutor extends Thread {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    @Getter @Setter
    private boolean extendedDebug;

    CloudExecutor() {
        super("Cloud Executor Thread");
//        setDaemon(true);
        this.extendedDebug = false;
    }

    @Override
    public void run() {
        while (byteCloud.isRunning) {
            if (extendedDebug) byteCloud.debug("queue size: " + queue.size());
            try {
                queue.take().run();
            } catch (InterruptedException ex) {
                byteCloud.debug(ex.getMessage());
            }
        }
        for(Runnable runnable : queue) {
            runnable.run();
        }
    }

    public boolean execute(Runnable runnable) {
        byteCloud.debug("queue size before add: "+queue.size());
        return queue.add(runnable);
    }

    public boolean execute(Runnable runnable, long sleepSeconds) {
        long check = System.currentTimeMillis() / 1000 + sleepSeconds;

        return execute(()-> {
            while (byteCloud.isRunning) {
                if(System.currentTimeMillis() / 1000 >= check) break;
            }
            runnable.run();
        });
    }
}
