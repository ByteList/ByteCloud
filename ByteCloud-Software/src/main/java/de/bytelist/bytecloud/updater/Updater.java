package de.bytelist.bytecloud.updater;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.jenkinsapi.JenkinsAPI;
import lombok.Setter;

import java.io.*;
import java.util.Objects;
import java.util.logging.Handler;

/**
 * Created by ByteList on 31.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Updater extends Thread {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    private final JenkinsAPI jenkinsAPI;
    @Setter
    private UpdateChannel channel;

    private final boolean shutdown;

    public Updater(UpdateChannel channel, boolean shutdown) {
        super("Updater Thread #"+System.currentTimeMillis()/1000);
        this.jenkinsAPI = new JenkinsAPI("apiUser", "Uf6UYSqSrgOGby01fSIe7dAkd1eSzVYggqH");
        this.channel = channel;
        this.shutdown = shutdown;

        start();
    }

    @Override
    public void run() {
        try {
            String loginCheck = jenkinsAPI.getLoginCorrect("https://kvm.bytelist.de/jenkins/");
            if(!loginCheck.equals(JenkinsAPI.CORRECT_LOGIN_VARIABLE)) {
                byteCloud.getLogger().warning("Cannot check for updates:");
                byteCloud.getLogger().warning(loginCheck);
                return;
            }
        } catch (Exception ex) {
            byteCloud.getLogger().warning("Cannot check for updates (ex):");
            ex.printStackTrace();
        }

        int currentBuildNumber = Integer.parseInt(byteCloud.getVersion().replace(".", ":").split(":")[2]);

        String url = lookUp(currentBuildNumber);

        if(url == null) {
            byteCloud.getLogger().info("No update found.");
            return;
        }

        try {
            File file = new File(".", "tempUpdate/");
            if(!file.exists()) {
                file.mkdir();
            }
            String path = url+"artifact/";
            byteCloud.getLogger().info(downloadFile(jenkinsAPI, path+"ByteCloud-Software/target/ByteCloud-Software.jar", "./tempUpdate/ByteCloud-Software.jar"));
            byteCloud.getLogger().info(downloadFile(jenkinsAPI, path+"ByteCloud-Plugin-Spigot/target/ByteCloud-Plugin-Spigot.jar", "./tempUpdate/ByteCloud-Plugin-Spigot.jar"));
            byteCloud.getLogger().info(downloadFile(jenkinsAPI, path+"ByteCloud-Plugin-Bungee/target/ByteCloud-Plugin-Bungee.jar", "./tempUpdate/ByteCloud-Plugin-Bungee.jar"));
            byteCloud.getLogger().info("Download successful! Moving files to their location...");

            byteCloud.getLogger().info(moveFile(new File("./tempUpdate/", "ByteCloud-Software.jar"), new File(".", "ByteCloud-Software-Updated.jar")));
            byteCloud.getLogger().info(moveFile(new File("./tempUpdate/", "ByteCloud-Plugin-Spigot.jar"), new File(EnumFile.GENERALS_PLUGINS.getPath(), "ByteCloud-Plugin-Spigot.jar")));
            byteCloud.getLogger().info(moveFile(new File("./tempUpdate/", "ByteCloud-Plugin-Bungee.jar"), new File(EnumFile.BUNGEE.getPath()+"plugins/", "ByteCloud-Plugin-Bungee.jar")));

            if(Objects.requireNonNull(file.listFiles()).length == 0) {
                file.delete();
            }

            Thread shutdownHook = new Thread(() -> {
                String[] param = {"sh", "update.sh"};
                try {
                    Runtime.getRuntime().exec(param);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            shutdownHook.setDaemon(true);
            Runtime.getRuntime().addShutdownHook(shutdownHook);

            if(shutdown) {
                byteCloud.getLogger().info("Update successful! Restarting cloud...");
                for (Handler handler : byteCloud.getLogger().getHandlers()) {
                    handler.close();
                }
                System.exit(0);
            } else {
                byteCloud.getLogger().info("Update successful!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String lookUp(int currentBuildNumber) {
        byteCloud.getLogger().info("Checking for new build...");

        String urlStable = "https://kvm.bytelist.de/jenkins/job/ByteCloud-v2/lastSuccessfulBuild/";
        String urlDev = "https://kvm.bytelist.de/jenkins/job/ByteCloud-v2-dev/lastSuccessfulBuild/";
        int lastStableBuildNumber = Integer.parseInt(jenkinsAPI.getBuildNumber(urlStable));
        int lastStableDevBuildNumber = Integer.parseInt(jenkinsAPI.getBuildNumber(urlDev));
        String lastVersion = byteCloud.getCloudConfig().getString("last-version");
        String lastVersionType = byteCloud.getCloudConfig().getString("last-version-type");
        String lastVersionStable = byteCloud.getCloudConfig().getString("last-version-stable");
        String updateChannel = this.channel.getChannel();


        if(updateChannel.equals("dev")) {
            if(byteCloud.isCurrentDevBuild()) {
                if(currentBuildNumber < lastStableDevBuildNumber) {
                    byteCloud.getLogger().info("Update found! [channel: dev] Current build: "+currentBuildNumber+" - New build: "+lastStableDevBuildNumber);
                    byteCloud.getCloudConfig()
                            .append("last-version", byteCloud.getVersion())
                            .append("last-version-type", "dev")
                            .saveAsConfig(byteCloud.getConfigFile());
                    return urlDev;
                }
            } else {
                byteCloud.getLogger().warning("Current channel is set to dev, but current version is a stable-build!");
                byteCloud.getLogger().warning("Try to look up for last version...");

                if(lastVersion.equals("-") || lastVersionType.equals("-")) {
                    byteCloud.getLogger().warning("Can not check last version, because last-version or last-version-type isn't set!");
                } else {
                    byteCloud.getLogger().info("Last version found! (v"+lastVersion+", last-type: "+lastVersionType+")");
                    byteCloud.getLogger().info("Checking last version type...");

                    switch (lastVersionType) {
                        case "dev":
                            byteCloud.getLogger().info("Last version type (eq. channel) is dev!");
                            byteCloud.getLogger().info("Checking build version...");
                            int lastVersionBuildNumber = Integer.parseInt(lastVersion.replace(".", ":").split(":")[2]);

                            if(lastVersionBuildNumber < lastStableDevBuildNumber) {
                                byteCloud.getLogger().info("Update found! [channel: dev] Last build: "+lastVersionBuildNumber+" - New build: "+lastStableDevBuildNumber);
                                byteCloud.getCloudConfig()
                                        .append("last-version", byteCloud.getVersion())
                                        .append("last-version-type", "stable")
                                        .saveAsConfig(byteCloud.getConfigFile());
                                return urlStable;
                            }
                            break;
                        case "stable":
                            byteCloud.getLogger().warning("Last version type (eq. channel) is stable!");
                            byteCloud.getLogger().info("Now the last dev build is downloading. ("+lastStableDevBuildNumber+")");
                            byteCloud.getCloudConfig()
                                    .append("last-version", byteCloud.getVersion())
                                    .append("last-version-type", "stable")
                                    .saveAsConfig(byteCloud.getConfigFile());
                            return urlDev;
                        default:
                            byteCloud.getLogger().warning("Unknown last version type (eq. channel)!");
                            byteCloud.getLogger().info("Now the last dev build is downloading. ("+lastStableDevBuildNumber+")");
                            byteCloud.getCloudConfig()
                                    .append("last-version", byteCloud.getVersion())
                                    .append("last-version-type", "stable")
                                    .saveAsConfig(byteCloud.getConfigFile());
                            return urlDev;
                    }
                }
            }
        } else {
            if(byteCloud.isCurrentDevBuild()) {
                byteCloud.getLogger().warning("Current channel is set to stable, but current version is a dev-build!");
                byteCloud.getLogger().warning("Try to look up for last version...");

                if(lastVersion.equals("-") || lastVersionType.equals("-")) {
                    byteCloud.getLogger().warning("Can not check last version, because last-version or last-version-type isn't set!");
                } else {
                    byteCloud.getLogger().info("Last version found! (v"+lastVersion+", last-type: "+lastVersionType+")");
                    byteCloud.getLogger().info("Checking last version type...");

                    switch (lastVersionType) {
                        case "dev":
                            byteCloud.getLogger().warning("Last version type (eq. channel) is dev!");
                            byteCloud.getLogger().info("Now the last stable build is downloading. ("+lastStableBuildNumber+")");
                            byteCloud.getCloudConfig()
                                    .append("last-version", byteCloud.getVersion())
                                    .append("last-version-type", "dev")
                                    .saveAsConfig(byteCloud.getConfigFile());
                            return urlStable;
                        case "stable":
                            byteCloud.getLogger().info("Last version type (eq. channel) is stable!");
                            byteCloud.getLogger().info("Checking build version...");

                            int lastVersionBuildNumber = Integer.parseInt(lastVersion.replace(".", ":").split(":")[2]);

                            if(lastVersionBuildNumber < lastStableBuildNumber) {
                                byteCloud.getLogger().info("Update found! [channel: stable] Last build: "+lastVersionBuildNumber+" - New build: "+lastStableBuildNumber);
                                byteCloud.getCloudConfig()
                                        .append("last-version", byteCloud.getVersion())
                                        .append("last-version-type", "dev")
                                        .saveAsConfig(byteCloud.getConfigFile());
                                return urlStable;
                            }
                            break;
                        default:
                            byteCloud.getLogger().warning("Unknown last version type (eq. channel)!");
                            byteCloud.getLogger().info("Try checking last stable version...");

                            if(lastVersionStable.equals("-")) {
                               byteCloud.getLogger().warning("Last stable version isn't set!");
                            } else {
                                int lastVersionStableBuildNumber = Integer.parseInt(lastVersionStable.replace(".", ":").split(":")[2]);

                                if(lastVersionStableBuildNumber < lastStableBuildNumber) {
                                    byteCloud.getLogger().info("Update found! [channel: stable] Last build: "+lastVersionStableBuildNumber+" - New build: "+lastStableBuildNumber);
                                    byteCloud.getCloudConfig()
                                            .append("last-version", byteCloud.getVersion())
                                            .append("last-version-type", "dev")
                                            .saveAsConfig(byteCloud.getConfigFile());
                                    return urlStable;
                                }
                            }
                            break;
                    }
                }
            } else {
                if(currentBuildNumber < lastStableBuildNumber) {
                    byteCloud.getLogger().info("Update found! [channel: stable] Current build: "+currentBuildNumber+" - New build: "+lastStableBuildNumber);
                    byteCloud.getCloudConfig()
                            .append("last-version", byteCloud.getVersion())
                            .append("last-version-type", "stable")
                            .saveAsConfig(byteCloud.getConfigFile());
                    return urlStable;
                }
            }

        }
        return null;
    }

    private String downloadFile(JenkinsAPI jenkinsAPI, String url, String downloadedFile) throws IOException {

        InputStream in = jenkinsAPI.getInputStream(url);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buf = new byte[1024];
        int n;
        System.out.println("Please wait...");
        System.out.println("File size: " + jenkinsAPI.getContentLength(url) + " bytes");
        try {
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] response = out.toByteArray();

        FileOutputStream fos = new FileOutputStream(downloadedFile);
        fos.write(response);
        fos.close();
        return "Downloaded " + url + " to " + downloadedFile;
    }

    private String moveFile(File file, File to) {
        if(file.renameTo(to)) {
            return "Moved file "+file.getName()+" to "+to.getPath();
        }
        return "Error while moving "+file.getName() + " to "+to.getPath();
    }

}
