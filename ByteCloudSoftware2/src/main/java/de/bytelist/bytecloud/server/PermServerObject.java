package de.bytelist.bytecloud.server;

import de.bytelist.bytecloud.file.EnumFile;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ByteList on 17.03.2017.
 */
public class PermServerObject {

    private PermServerObject permanentServerObject;
    private List<String> elements;

    private Object element;
    private
    @Getter
    String elementName;

    public PermServerObject(String server) {
        File dir = new File(EnumFile.SERVERS_PERMANENT.getPath(), server);
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
        permanentServerObject = this;
    }


    public PermServerObject get(String element) {
        elementName = element;
        for (String parm : permanentServerObject.elements) {
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
