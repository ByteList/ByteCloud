package de.bytelist.bytecloud;

/**
 * Created by ByteList on 12.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class KeepAliveTask extends Thread {

    @Override
    public void run() {
        System.out.println("Keep Alive Thread is running.");
        while (true) {
            if(!ByteCloud.getInstance().isRunning) break;
        }
        System.out.println("Keep Alive Thread was stopped.");
    }
}
