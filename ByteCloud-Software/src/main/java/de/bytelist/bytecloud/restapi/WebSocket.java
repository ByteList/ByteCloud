package de.bytelist.bytecloud.restapi;

import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.log.CloudLogger;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by ByteList on 29.09.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public class WebSocket {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    private final String uid;

    private final CloudLogger logger;

    private final int port;
    private final boolean local, ok;

    private Server jettyServer;

    public WebSocket(CloudLogger logger, int port, boolean local) {
        this.uid = byteCloud.getCloudConfig().getString("web-auth");

        this.ok = byteCloud.getWebService().isOk();
        this.logger = logger;
        this.port = port;
        this.local = local;
    }

    public void start() {
        if(!ok) {
            logger.warning("Can not start web socket, because authentication wasn't ok.");
            return;
        }

        this.jettyServer = new Server(new InetSocketAddress((local ? "127.0.0.1" : "0.0.0.0"), port));

        this.jettyServer.setHandler(new AbstractHandler() {
            @Override
            public void handle(String s, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setContentType("text/plain;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                baseRequest.setHandled(true);

                if(baseRequest.getParameter("uid") == null || !baseRequest.getParameter("uid").equals(uid)) {
                    response.getWriter().println("not-authenticated");
                } else {
                    if(baseRequest.getParameter(""))
                }
            }
        });
        try {
            this.jettyServer.start();
            this.jettyServer.join();
            logger.info("Web-Socket started!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
