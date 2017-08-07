package de.bytelist.bytecloud.installer;

import de.bytelist.bytecloud.ByteCloud;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ByteList on 31.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Installer {

    public boolean isFinished, isSuccessful;
    private boolean skip;
    private final String fileName = "temp-sd651gdowncld.zip";
    private final File outputFolder = new File(".");


    public Installer() {
        isFinished = false;
        isSuccessful = false;
        skip = false;
    }

    public boolean isUpdated() {
        return new File("./Cloud/", "cloud.properties").exists();
    }

    public void downloadFiles() {
        if (!isUpdated()) {
            try {
                System.out.println("Start downloading...");
                URL link = new URL("https://med.bytelist.de/cloud/v" + ByteCloud.getInstance().getVersion().replace(".", "_") + ".zip");

                HttpURLConnection conn = null;
                long fileSize;
                try {
                    conn = (HttpURLConnection) link.openConnection();
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
                    InputStream in = new BufferedInputStream(link.openStream());
                    ByteArrayOutputStream out = new ByteArrayOutputStream();


                    byte[] buf = new byte[1024];
                    int n;
                    System.out.println("Please wait...");
                    System.out.println("File size: " + fileSize + " bytes");
                    int i = 0;
                    while (-1 != (n = in.read(buf))) {
                        i++;
                        out.write(buf, 0, n);
                    }
                    out.close();
                    in.close();
                    byte[] response = out.toByteArray();

                    FileOutputStream fos = new FileOutputStream(fileName);
                    fos.write(response);
                    fos.close();
                    //End download code

                    System.out.println("Download finished.");
                } else {
                    System.err.println("Can't download files! file size < 0");
                    skip = true;
                    isFinished = true;
                }
            } catch (IOException ex) {
                System.err.println("Can't download files! Maybe doesn't exists?");
                skip = true;
            }
        }
    }

    public void extractFiles() {
        if (!skip) {
            try {
                System.out.println("Start extracting files...");
                byte[] buffer = new byte[1024];

                if (!outputFolder.exists()) {
                    outputFolder.mkdir();
                }

                ZipInputStream zis = new ZipInputStream(new FileInputStream(fileName));
                ZipEntry ze = zis.getNextEntry();

                while (ze != null) {

                    String fileName = ze.getName();
                    File newFile = new File(outputFolder + File.separator + fileName);

                    new File(newFile.getParent()).mkdirs();

                    // Check the file type.
                    if(fileName.contains(".")) {
                        FileOutputStream fos = new FileOutputStream(newFile);

                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }

                        fos.close();
                    } else {
                        newFile.mkdirs();
                    }

                    ze = zis.getNextEntry();
                }

                zis.closeEntry();
                zis.close();


                File f = new File(fileName);
                if(f.exists())
                    f.delete();

                System.out.println("Extracting finished.");
                isSuccessful = true;
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                isFinished = true;
            }
        } else {
            System.out.println("** Skipping extraction **");
            isFinished = true;
        }
    }
}
