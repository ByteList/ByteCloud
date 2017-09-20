package de.bytelist.bytecloud;

import de.bytelist.bytecloud.file.EnumFile;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Created by ByteList on 27.01.2017.
 */
public class CloudProperties extends Properties {

    public CloudProperties() {
        File file = new File(EnumFile.CLOUD.getPath(), "cloud.properties");
        if (file.exists()) {
            try {
                FileInputStream localFileInputStream = new FileInputStream(file);

                this.load(localFileInputStream);
                localFileInputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            ByteCloud.getInstance().getLogger().log(Level.CONFIG, "No properties file found! Creating one...");

            this.setProperty("mongo-password", "password");
            this.setProperty("mongo-user", "user");
            this.setProperty("mongo-host", "localhost");
            this.setProperty("mongo-database", "cloud");
            this.setProperty("web-port", "8090");
            this.setProperty("socket-port", "4213");
            this.setProperty("spigot-version", "1.9.4");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            try {
                assert out != null;
                this.store(out, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteCloud.getInstance().getLogger().log(Level.CONFIG, "Properties file created!");
        }
    }
}
