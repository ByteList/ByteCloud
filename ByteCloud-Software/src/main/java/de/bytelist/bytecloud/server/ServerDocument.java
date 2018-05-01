package de.bytelist.bytecloud.server;

import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ByteList on 01.05.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ServerDocument {

    private ServerDocument serverDocument;
    private List<String> elements;

    private Object element;
    @Getter
    private String elementName;

    public ServerDocument(File dir) {
        File file = new File(dir, "settings.bci");
        if (!file.exists())
            try {
                throw new FileNotFoundException("Can not find settings.bci");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        elements = new ArrayList<>();

        try {
            elements.addAll(FileUtils.readLines(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverDocument = this;
    }


    public ServerDocument get(String element) {
        elementName = element;
        for (String parm : serverDocument.elements) {
            if (parm.split("=")[0].equalsIgnoreCase(element))
                this.element = parm.split("=")[1];
        }
        return this;
    }

    public String getAsString() {
        return element.toString();
    }

    public int getAsInt() {
        return Integer.parseInt(element.toString());
    }

    public boolean getAsBoolean() {
        return Boolean.parseBoolean(element.toString());
    }
}
