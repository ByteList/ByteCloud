package de.bytelist.bytecloud.bungee;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.bytecloud.packet.cloud.PacketOutCloudInfo;
import de.bytelist.bytecloud.packet.cloud.PacketOutRegisterServer;
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

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    /**
     * Gets the id from this bungee.
     */
    @Getter
    private String bungeeId;
    /**
     * Gets the directory {@link File} from this bungee.
     */
    @Getter
    private final File directory;
    /**
     * Gets the current {@link Process} from this bungee.
     */
    @Getter
    private Process process;
    /**
     * Gets the max ram from this bungee.
     */
    @Getter
    private int ramM;

    /**
     * Gets is a start/stop executed by a command at {@link de.bytelist.bytecloud.console.commands.BungeeCommand}.
     */
    public boolean execByCommand;

    /**
     * Gets is the bungee started.
     */
    @Getter
    private boolean started;

    private Runnable runStartSuccess, runStopSuccess;

    /**
     * Initialise the bungee.
     */
    public Bungee() {
        this.execByCommand = false;
        this.bungeeId = "Bungee-1";
        this.directory = new File(EnumFile.BUNGEE.getPath());
        this.ramM = 1024;
        try {
            FileUtils.copyFile(new File(EnumFile.CLOUD.getPath(), "config.json"), new File(this.getDirectory(), "plugins/ByteCloud/config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the bungee.
     * @param runSuccess the runnable gets execute after a successful start
     */
    public void startBungee(Runnable runSuccess) {
        this.runStartSuccess = runSuccess;
        if(!byteCloud.getCloudExecutor().execute(()-> {
            if (!isRunning()) {
                byteCloud.getLogger().info("Bungee is starting.");
                String[] param = { "java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=50", "-Xmn2M",
                        "-Xmx"+String.valueOf(ramM)+"M", "-Dde.bytelist.bytecloud.servername="+bungeeId,
                        "-Dde.bytelist.bytecloud.connectServer="+byteCloud.getServerIdOnConnect(),
                        "-Djline.terminal=jline.UnsupportedTerminal", "-DIReallyKnowWhatIAmDoingISwear=true", "-jar", "BungeeCord.jar" };
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

    /**
     * Stops the bungee.
     * @param runSuccess the runnable gets execute after a successful stop
     * @param runError the runnable gets execute after an error occurred at the stop
     */
    public void stopBungee(Runnable runSuccess, Runnable runError) {
        this.runStopSuccess = runSuccess;
        if(!byteCloud.getCloudExecutor().execute(()-> {
            try {
                if(isRunning()) {
                    byteCloud.getLogger().info("Bungee is stopping.");
                    Thread.sleep(3000L);
                    this.process.getOutputStream().write("end\n".getBytes());
                    this.process.getOutputStream().flush();
                }
            } catch (Exception ex) {
                System.err.println("Error while stopping bungee:");
                ex.printStackTrace();
                runError.run();
            }

        })) byteCloud.getLogger().warning("CloudExecutor returns negative statement while stopping bungee "+bungeeId);
    }

    /**
     * Stops the bungee.
     */
    public void stopBungee() {
        this.stopBungee(()-> {}, ()-> {});
    }

    /**
     * Gets executed when the bungee sent the {@link de.bytelist.bytecloud.packet.bungee.PacketInBungee} packet.
     * Set the bungee to started.
     */
    public void onStart() {
        if(isRunning()) {
            byteCloud.getCloudServer().sendPacket(bungeeId, new PacketOutCloudInfo(byteCloud.getVersion(), byteCloud.getCloudStarted(), byteCloud.isRunning));
            byteCloud.getLogger().info("Bungee started.");
            this.started = true;
            this.runStartSuccess.run();
            if(this.execByCommand) {
                for(Server server : byteCloud.getServerHandler().getServers()) {
                    byteCloud.getCloudServer().sendPacket(byteCloud.getBungee().getBungeeId(), new PacketOutRegisterServer(server.getServerId(), server.getPort()));
                }
                this.execByCommand = false;
            }
        }
    }

    /**
     * Gets executed when the bungee sent the {@link de.bytelist.bytecloud.packet.bungee.PacketInBungeeStopped} packet.
     * Destroy the process from the bungee and set the bungee to stopped.
     */
    public void onStop() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(isRunning()) {
            this.process.destroy();
            this.process = null;
        }
        byteCloud.getLogger().info("Bungee stopped.");
        this.runStopSuccess.run();
        this.started = false;
    }

    /**
     * Gets is the bungee running.
     * @return is the bungee running
     */
    public boolean isRunning() {
        return this.process != null && this.process.isAlive();
    }

    /**
     * Execute a command at the bungee.
     * @param command to get execute
     */
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

    /**
     * Gets the current bungee id.
     * @return the current bungee id
     */
    @Override
    public String getServerId() {
        return bungeeId;
    }
}
