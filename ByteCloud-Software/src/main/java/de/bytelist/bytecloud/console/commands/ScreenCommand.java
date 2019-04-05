package de.bytelist.bytecloud.console.commands;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.console.Command;
import de.bytelist.bytecloud.server.Server;

/**
 * Created by ByteList on 10.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class ScreenCommand extends Command {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    public ScreenCommand() {
        super("screen", "Screen commands");
    }

    @Override
    public void execute(String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("leave")) {
                byteCloud.getScreenManager().closeScreen();
//                System.out.println("Starting cloud system."+
//                        "\n"+ AnsiColor.CYAN +"\n" +
//                        "   ____        _        _____ _                 _ \n" +
//                        "  |  _ \\      | |      / ____| |               | |\n" +
//                        "  | |_) |_   _| |_ ___| |    | | ___  _   _  __| |\n" +
//                        "  |  _ <| | | | __/ _ \\ |    | |/ _ \\| | | |/ _` |\n" +
//                        "  | |_) | |_| | ||  __/ |____| | (_) | |_| | (_| |\n" +
//                        "  |____/ \\__, |\\__\\___|\\_____|_|\\___/ \\__,_|\\__,_|\n" +
//                        "          __/ | T I G E R\n" +
//                        "         |___/                 b y   B y t e L i s t\n" +
//                        "\n\n");
                byteCloud.getLogger().info("** You left the screen.");
                return;
            }

            if(byteCloud.getBungee().getBungeeId().startsWith(args[0])) {
                if(byteCloud.getBungee().isRunning()) {
                    byteCloud.getScreenManager().joinNewScreen(byteCloud.getBungee());
                } else {
                    byteCloud.getLogger().info("Bungee isn't running!");
                }
                return;
            }

            Server server = byteCloud.getServerHandler().getServer(args[0]);

            if(server != null) {
                byteCloud.getScreenManager().joinNewScreen(server);
                return;
            }

            byteCloud.getLogger().info("Could not find a server with id: "+args[0]);
        }
    }
}
