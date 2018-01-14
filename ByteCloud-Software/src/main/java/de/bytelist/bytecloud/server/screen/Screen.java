package de.bytelist.bytecloud.server.screen;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Created by ByteList on 10.01.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class Screen {

    @Getter
    private IScreen screen;
    @Getter
    private Thread handled;

    public Screen() {
        this.screen = null;
        this.handled = null;
    }

    public void joinNewScreen(IScreen screen) {
        this.closeScreen();
        this.screen = screen;
        this.handled = new Thread(() -> {
            try {
                InputStreamReader in = new InputStreamReader(screen.getProcess().getInputStream(), StandardCharsets.UTF_8);
                BufferedReader reader = new BufferedReader(in);

                String input;
                while((input = reader.readLine()) != null) {
                    if(!input.equals(">") && !input.equals(">>"))
                        System.out.println("#%scr3En%#"+input);
                }

                this.handled = null;
                this.screen = null;
            } catch (IOException ex) {
                this.closeScreen();
            }

        });
        this.handled.start();
    }

    public Screen closeScreen() {
        if (this.handled != null) {
            this.handled.stop();
            this.handled = null;
        }

        if (this.screen != null) {
            this.screen = null;
        }

        return this;
    }

    public Screen checkAndRemove(IScreen screen) {
        return this.screen != null && this.screen.getServerId().equals(screen.getServerId()) ? this.closeScreen() : this;
    }

    public void command(String command) {
        if (this.screen != null) {
            this.screen.runCommand(command);
        }
    }
}
