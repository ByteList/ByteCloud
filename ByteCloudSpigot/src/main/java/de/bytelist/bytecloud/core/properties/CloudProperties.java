package de.bytelist.bytecloud.core.properties;


import org.bukkit.Bukkit;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

/**
 * Created by ByteList on 27.01.2017.
 */
public class CloudProperties extends Properties {

    private static CloudProperties cloudProperties;

    public static void load() {
        CloudProperties cloudProperties = new CloudProperties();
        String name = "cloud.properties";
        File dir = new File("plugins/", "ByteCloud/");
        if(!dir.exists()) dir.mkdirs();
        File file = new File(dir, name);

        if(file.exists()) {
            try {
                FileInputStream localFileInputStream = new FileInputStream(file);

                cloudProperties.load(localFileInputStream);
                localFileInputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            Bukkit.getLogger().log(Level.CONFIG, "[Cloud/Properties] No properties file found!");
        }
        CloudProperties.cloudProperties = cloudProperties;
    }

    public static CloudProperties getCloudProperties() {
        return cloudProperties;
    }
}
