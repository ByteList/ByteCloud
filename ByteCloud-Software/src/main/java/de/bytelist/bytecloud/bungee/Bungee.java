package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutCloudInfo;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutRegisterServer;
import de.bytelist.bytecloud.server.Server;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Bungee {

    @Getter
    private String bungeeId;
    @Getter
    private final File directory;
    @Getter
    private Process process;

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public boolean execByCommand;

    public Bungee() {
        this.execByCommand = false;
        this.bungeeId = "Bungee-1";
        this.directory = new File(EnumFile.BUNGEE.getPath());
        try {
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "cloud.properties"), new File(this.getDirectory(), "plugins/ByteCloud/cloud.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startBungee() {
        byteCloud.getCloudExecutor().execute(()-> {
            if (!isRunning()) {
                byteCloud.getLogger().info("Bungee is starting.");
                String[] param = { "java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-Xmn2M", "-Xmx1024M", "-Dde.bytelist.bytecloud.servername="+bungeeId, "-jar", "BungeeCord.jar", "noconsole" };
                ProcessBuilder pb = new ProcessBuilder(param);
                pb.directory(directory);
                try {
                    process = pb.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopBungee() {
        byteCloud.getCloudExecutor().execute(()-> {
            if(isRunning()) {
                byteCloud.getLogger().info("Bungee is stopping.");
                try {
                    Thread.sleep(3000L);
                    this.process.getOutputStream().write("cloudend\n".getBytes());
                    this.process.getOutputStream().flush();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                byteCloud.getLogger().info("Bungee stopped.");
            }
        });
    }

    public void onStart() {
        if(isRunning()) {
            byteCloud.getCloudServer().sendPacket(bungeeId, new PacketOutCloudInfo(byteCloud.getVersion(), byteCloud.getCloudStarted(), byteCloud.isRunning));
            byteCloud.getLogger().info("Bungee started.");
            if(this.execByCommand) {
                for(Server server : byteCloud.getServerHandler().getServers()) {
                    byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), new PacketOutRegisterServer(server.getServerId(), server.getPort()));
                }
                this.execByCommand = false;
            }
        }
    }

    public void onStop() {
        try {
            Thread.sleep(15000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(isRunning()) {
            this.process.destroy();
            this.process = null;
        }
    }

    public boolean isRunning() {
        return this.process != null && this.process.isAlive();
    }

}
