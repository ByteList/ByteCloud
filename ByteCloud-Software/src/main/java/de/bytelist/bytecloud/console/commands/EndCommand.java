package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.updater.UpdateChannel;
import de.bytelist.bytecloud.updater.Updater;

/**
 * Created by ByteList on 14.05.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class EndCommand extends Command {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public EndCommand() {
        super("end", "shutdown the cloud");
    }

    @Override
    public void execute(String[] args) {

        if(args.length == 1) {
            if(args[0].startsWith("-update")) {
                UpdateChannel updateChannel;
                if(args[0].contains("=")) updateChannel = UpdateChannel.getUpdateChannel(args[0].split("=")[1]);
                else updateChannel = UpdateChannel.getUpdateChannel(byteCloud.getCloudConfig().getString("update-channel"));

                Updater updater = new Updater(updateChannel, false);
                while (true) {
                    if(!updater.isAlive()) break;
                }
                return;
            }
            byteCloud.getLogger().info("Do you mean: \"end -update\" ?");
            return;
        }
        ByteCloud.getInstance().stop();
    }
}
