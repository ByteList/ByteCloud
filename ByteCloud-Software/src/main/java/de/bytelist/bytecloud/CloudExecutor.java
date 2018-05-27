package de.bytelist.bytecloud;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ByteList on 22.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class CloudExecutor extends Thread {

    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    CloudExecutor() {
        super("Cloud Executor Thread");
        setDaemon(true);
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            Runnable runnable;
            try {
                runnable = queue.take();
            } catch (InterruptedException ex) {
                if(ByteCloud.getInstance().isDebug()) {
                    ByteCloud.getInstance().debug(ex.getMessage());
                }
                continue;
            }
            runnable.run();
        }
        for(Runnable runnable : queue) {
            runnable.run();
        }
    }

    public boolean execute(Runnable runnable) {
        if(ByteCloud.getInstance().isDebug()) {
            ByteCloud.getInstance().debug("queue size before add: "+queue.size());
        }
        return !isInterrupted() && queue.add(runnable);
    }

    public boolean execute(Runnable runnable, long sleepSeconds) {
        return execute(()-> {
            try {
                Thread.sleep(sleepSeconds*1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
        });
    }
}
