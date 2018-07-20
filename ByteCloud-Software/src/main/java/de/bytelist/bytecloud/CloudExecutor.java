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

    /**
     * If true it send some more information from the queue.
     */
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

    /**
     * Put a runnable into the queue while cloud is running otherwise the runnable will executed directly.
     * @param runnable to execute
     * @return successful added to queue
     */
    public boolean execute(Runnable runnable) {
        if(!byteCloud.isRunning) {
            runnable.run();
            return true;
        }
        byteCloud.debug("queue size before add: "+queue.size());
        return queue.add(runnable);
    }

    /**
     * Execute a runnable after the sleep time.
     *
     * @param runnable to execute
     * @param sleepSeconds the sleeping time in seconds
     * @return successful queue add
     */
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
