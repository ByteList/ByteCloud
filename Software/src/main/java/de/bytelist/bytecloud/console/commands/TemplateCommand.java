package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.file.EnumFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by ByteList on 10.06.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class TemplateCommand extends Command {

    public TemplateCommand() {
        super("template", "server template commands");
    }
    
    private final Logger logger = ByteCloud.getInstance().getLogger();

    @Override
    public void execute(String[] args) {

        if(args.length == 9) {
            if(args[0].equalsIgnoreCase("create")) {
                String name = args[1].toUpperCase();
                String prefix = args[2];
                int amount = Integer.parseInt(args[3]);
                int max = Integer.parseInt(args[4]);
                int player = Integer.parseInt(args[5]);
                int spectator = Integer.parseInt(args[6]);
                int ramM = Integer.parseInt(args[7]);
                int port = Integer.parseInt(args[8]);

                File dir = new File(EnumFile.TEMPLATES.getPath(), name);
                if(!dir.exists()) dir.mkdirs();
                File file = new File(dir, "settings.bci");
                if (!file.exists())
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                ArrayList<String> lines = new ArrayList<>();
                lines.add("prefix=" + prefix);
                lines.add("amount=" + amount);
                lines.add("max=" + max);
                lines.add("player=" + player);
                lines.add("spectator=" + spectator);
                lines.add("ram=" + ramM);
                lines.add("port=" + port);

                try {
                    FileUtils.writeLines(file, lines);
                    logger.info("Template " + name + " created!");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("info")) {
                String name = args[1].toUpperCase();
                File file = new File(EnumFile.TEMPLATES.getPath()+name, "settings.bci");

                if (!file.exists()) {
                    logger.info("Can not find settings.bci for template " + name+"!");
                } else {
                    logger.info("Template information for " + name + ":");
                    try {
                        ArrayList<Object> lines = new ArrayList<Object>(FileUtils.readLines(file));
                        for (Object l : lines) {
                            logger.info(l.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
            if(args[0].equalsIgnoreCase("delete")) {
                String name = args[1].toUpperCase();
                File file = new File(EnumFile.TEMPLATES.getPath(), name);

                if (!file.exists()) {
                    logger.info("Can not find template directory for " + name);
                } else {
                    try {
                        FileUtils.deleteDirectory(file);
                        logger.info("Template " + name + " deleted!");
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }

        System.out.println("Using template command:");
        System.out.println("template create <name> <prefix> <startAmount> <maxServ> <player> <spectator> <ram> <startPort>");
        System.out.println("template delete <name>");
        System.out.println("template info <name>");
    }
}
