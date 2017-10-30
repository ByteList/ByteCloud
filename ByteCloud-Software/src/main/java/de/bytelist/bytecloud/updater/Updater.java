package de.bytelist.bytecloud.updater;

import de.bytelist.bytecloud.ByteCloud;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ByteList on 31.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Updater {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    private final int currentBuildNumber;
    private final int lastStableBuildNumber;


    public Updater(int currentBuildNumber) {
        this.currentBuildNumber = currentBuildNumber;

        URL url = null;
        BufferedReader bufferedReader;
        InputStreamReader inputStreamReader = null;
        String buildNumber = "-1";
        try {
            url = new URL("http://79.133.45.202:8080/job/ByteCloud%20v2/lastStableBuild/buildNumber");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if(url != null) {
            try {
                inputStreamReader = new InputStreamReader(url.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(inputStreamReader != null) {
                bufferedReader = new BufferedReader(inputStreamReader);
                try {
                    buildNumber = bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        inputStreamReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        this.lastStableBuildNumber = Integer.parseInt(buildNumber);
    }

    public boolean isUpdated() {
        if(this.lastStableBuildNumber == -1) {
            byteCloud.getLogger().warning("Can't check for update! lastStableBuildNumber is -1");
            return true;
        } else {
            return this.currentBuildNumber == this.lastStableBuildNumber;
        }
    }

    public void downloadFiles() {
        downloadFile("http://79.133.45.202:8080/job/ByteCloud%20v2/" +
                "lastStableBuild/de.bytelist.bytecloud$ByteCloud-Software/artifact/" +
                "de.bytelist.bytecloud/ByteCloud-Software/2.0/ByteCloud-Software-2.0.jar", "ByteCloud.jar");
    }

    public boolean downloadFile(String file, String fileName) {
        URL url;
        byteCloud.getLogger().info("Downloading ByteCloud-Software-2.0.jar ...");
        try {
            url = new URL(file);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        HttpURLConnection conn = null;
        long fileSize;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            fileSize = conn.getContentLengthLong();
        } catch (IOException e) {
            fileSize = -1;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        if (fileSize > 0) {
            InputStream in = null;
            try {
                in = new BufferedInputStream(url.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();


            byte[] buf = new byte[1024];
            int n;
            System.out.println("Please wait...");
            System.out.println("File size: " + fileSize + " bytes");
            int i = 0;
            try {
                while (-1 != (n = in.read(buf))) {
                    i++;
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

//            FileOutputStream fos = new FileOutputStream(fileName);
//            fos.write(response);
//            fos.close();
            //End download code

            System.out.println("Download finished.");
        }
        return false;
    }
}
