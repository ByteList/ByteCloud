package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutCloudInfo;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutStopBungee;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static de.bytelist.bytecloud.network.NetworkManager.getCloudServer;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Bungee {

    @Getter
    private String bungeeId;
    @Getter
    private boolean running;
    @Getter
    private final File directory;
    @Getter
    private Process process;
    @Getter
    private Thread thread;

    public Bungee() {
        this.bungeeId = "Bungee-1";
        this.running = false;
        this.directory = new File(EnumFile.BUNGEE.getPath());
        try {
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "cloud.properties"), new File(this.getDirectory(), "plugins/ByteCloud/cloud.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startBungee() {
        if(!running) {
            ByteCloud.getInstance().getLogger().info("Bungee is starting.");
            this.thread = new Thread(bungeeId+" Thread") {
                @Override
                public void run() {
                    running = true;
                    if (process == null) {
                        String[] param = { "java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-Xmn2M", "-Xmx1024M", "-Dde.bytelist.bytecloud.servername="+bungeeId, "-jar", "BungeeCord.jar" };
                        ProcessBuilder pb = new ProcessBuilder(param);
                        pb.directory(directory);
                        try {
                            process = pb.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            this.thread.start();
        }
    }

    public void stopBungee() {
        if(running) {
            ByteCloud.getInstance().getLogger().info("Bungee is stopping.");
            getCloudServer().sendPacket(bungeeId, new PacketOutStopBungee());
        }
    }

    public void onStart() {
        if(running) {
            getCloudServer().sendPacket(bungeeId, new PacketOutCloudInfo());
            ByteCloud.getInstance().getLogger().info("Bungee started.");
        }
    }

    public void onStop() {
        if(running) {
            if(process != null) {
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.process.destroy();
                this.process = null;
                this.thread.interrupt();
                this.thread = null;
            }
            this.running = false;
            System.out.println("Bungee stopped.");
        }
    }
}
