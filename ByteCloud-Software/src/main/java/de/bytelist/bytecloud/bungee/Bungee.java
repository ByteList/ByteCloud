package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutCloudInfo;
import de.bytelist.bytecloud.network.cloud.packet.PacketOutRegisterServer;
import de.bytelist.bytecloud.server.Server;
import de.bytelist.bytecloud.server.screen.IScreen;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by ByteList on 11.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Bungee implements IScreen {

    @Getter
    private String bungeeId;
    @Getter
    private final File directory;
    @Getter
    private Process process;
    @Getter
    private int ramM;

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public boolean execByCommand;

    public Bungee() {
        this.execByCommand = false;
        this.bungeeId = "Bungee-1";
        this.directory = new File(EnumFile.BUNGEE.getPath());
        this.ramM = 1024;
        try {
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "cloud.properties"), new File(this.getDirectory(), "plugins/ByteCloud/cloud.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startBungee() {
        if(!byteCloud.getCloudExecutor().execute(()-> {
            if (!isRunning()) {
                byteCloud.getLogger().info("Bungee is starting.");
                String[] param = { "java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-Xmn2M", "-Xmx"+String.valueOf(ramM)+"M", "-Dde.bytelist.bytecloud.servername="+bungeeId, "-Djline.terminal=jline.UnsupportedTerminal", "-DIReallyKnowWhatIAmDoingISwear=true", "-jar", "BungeeCord.jar" };
                ProcessBuilder pb = new ProcessBuilder(param);
                pb.directory(directory);
                try {
                    process = pb.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })) byteCloud.getLogger().warning("CloudExecutor returns negative statement while starting bungee "+bungeeId);
    }

    public void stopBungee() {
        if(!byteCloud.getCloudExecutor().execute(()-> {
            if(isRunning()) {
                byteCloud.getLogger().info("Bungee is stopping.");
                try {
                    Thread.sleep(3000L);
                    this.process.getOutputStream().write("end\n".getBytes());
                    this.process.getOutputStream().flush();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                byteCloud.getLogger().info("Bungee stopped.");
            }
        })) byteCloud.getLogger().warning("CloudExecutor returns negative statement while stopping bungee "+bungeeId);
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

    @Override
    public void runCommand(String command) {
        String x = command + "\n";
        if (this.process != null) {
            try {
                this.process.getOutputStream().write(x.getBytes());
                this.process.getOutputStream().flush();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }
    }

    @Override
    public String getServerId() {
        return bungeeId;
    }
}
