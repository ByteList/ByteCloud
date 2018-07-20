package de.bytelist.bytecloud.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by ByteList on 02.12.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class FileMethods {

    /**
     * Put all files into a zip file.
     * @param zipFile the final file path
     * @param files all files
     * @return if the compress was successful
     */
    public static boolean compressZipFile(String zipFile, List<File> files) {
        byte[] buffer = new byte[1024];

        try{
            FileOutputStream fos = new FileOutputStream(zipFile+".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            for(File file : files) {
                ZipEntry ze = new ZipEntry(file.getName());
                zos.putNextEntry(ze);
                FileInputStream in = new FileInputStream(file);

                int len;
                while ((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
            }

            zos.closeEntry();
            zos.close();

            return true;

        }catch(IOException ex){
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes all listed files
     * @param files to get deleted
     */
    public static void deleteFiles(List<File> files) {
        for(File file : files) {
            file.delete();
        }
    }
}
