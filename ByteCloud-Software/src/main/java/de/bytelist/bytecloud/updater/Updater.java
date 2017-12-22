package de.bytelist.bytecloud.updater;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.file.EnumFile;
import de.bytelist.jenkinsapi.JenkinsAPI;

import java.io.*;
import java.util.logging.Handler;

/**
 * Created by ByteList on 31.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Updater {

    public Updater() {
        ByteCloud byteCloud = ByteCloud.getInstance();
        JenkinsAPI jenkinsAPI = new JenkinsAPI("apiUser", "Uf6UYSqSrgOGby01fSIe7dAkd1eSzVYggqH");

        String loginCheck = jenkinsAPI.getLoginCorrect("https://vs.bytelist.de/jenkins/");
        if(!loginCheck.equals(JenkinsAPI.CORRECT_LOGIN_VARIABLE)) {
            byteCloud.getLogger().warning("Cannot check for updates:");
            byteCloud.getLogger().warning(loginCheck);
            return;
        }

        int currentBuildNumber = Integer.parseInt(byteCloud.getVersion().replace(".", ":").split(":")[2]);
        int lastStableBuildNumber = Integer.parseInt(jenkinsAPI.getBuildNumber("https://vs.bytelist.de/jenkins/job/ByteCloud%20v2/lastSuccessfulBuild/"));

        if(currentBuildNumber < lastStableBuildNumber) {
            byteCloud.getLogger().info("Update found! Current build: "+currentBuildNumber+" - New build: "+lastStableBuildNumber);
            byteCloud.getLogger().info("Start downloading...");
            try {
                String path = "https://vs.bytelist.de/jenkins/job/ByteCloud%20v2/lastSuccessfulBuild/artifact/";
                byteCloud.getLogger().info(downloadFile(jenkinsAPI, path+"ByteCloud-Software/target/ByteCloud-Software.jar", "./tempUpdate/ByteCloud-Software.jar"));
                byteCloud.getLogger().info(downloadFile(jenkinsAPI, path+"ByteCloud-Plugin-Spigot/target/ByteCloud-Plugin-Spigot.jar", "./tempUpdate/ByteCloud-Plugin-Spigot.jar"));
                byteCloud.getLogger().info(downloadFile(jenkinsAPI, path+"ByteCloud-Plugin-Bungee/target/ByteCloud-Plugin-Bungee.jar", "./tempUpdate/ByteCloud-Plugin-Bungee.jar"));
                byteCloud.getLogger().info(downloadFile(jenkinsAPI, path+"ByteCloud-Plugin-Fallback/target/ByteCloud-Plugin-Fallback.jar", "./tempUpdate/ByteCloud-Plugin-Fallback.jar"));
                byteCloud.getLogger().info("Download successful! Moving files to their location...");

                byteCloud.getLogger().info(moveFile(new File("./tempUpdate/", "ByteCloud-Software.jar"), new File(".", "ByteCloud-Software-Updated.jar")));
                byteCloud.getLogger().info(moveFile(new File("./tempUpdate/", "ByteCloud-Plugin-Spigot.jar"), new File(EnumFile.GENERALS_PLUGINS.getPath(), "ByteCloud-Plugin-Spigot.jar")));
                byteCloud.getLogger().info(moveFile(new File("./tempUpdate/", "ByteCloud-Plugin-Bungee.jar"), new File(EnumFile.BUNGEE.getPath()+"plugins/", "ByteCloud-Plugin-Bungee.jar")));
                byteCloud.getLogger().info(moveFile(new File("./tempUpdate/", "ByteCloud-Plugin-Fallback.jar"), new File("./Fallback-Server/plugins/", "ByteCloud-Plugin-Fallback.jar")));

                if(new File("./", "tempUpdate/").listFiles().length == 0) {
                    new File("./", "tempUpdate/").delete();
                }
                byteCloud.getLogger().info("Update successful! Restarting cloud...");
                for (Handler handler : byteCloud.getLogger().getHandlers()) {
                    handler.close();
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
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            byteCloud.getLogger().info("No update found.");
        }
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
        System.out.println("Downloaded.");
        return "Downloading" + url + " to " + downloadedFile;
    }

    private String moveFile(File file, File to) {
        if(file.renameTo(to)) {
            return "Moved file "+file.getName()+" to "+to.getPath();
        }
        return "Error while moving "+file.getName() + " to "+to.getPath();
    }
}
