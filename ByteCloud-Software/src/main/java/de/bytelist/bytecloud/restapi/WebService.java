package de.bytelist.bytecloud.restapi;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import de.bytelist.bytecloud.ByteCloud;
import de.bytelist.bytecloud.log.CloudLogger;
import org.apache.http.HttpRequest;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by nemmerich on 07.09.2018.
 * <p>
 * Copyright by nemmerich - https://bytelist.de/
 */
public class WebService {

    private final ByteCloud byteCloud = ByteCloud.getInstance();

    private final String uid;

    private final CloudLogger logger;

    private final int port;
    private final boolean local, ok;

    private HttpServer httpServer;

    private final String HEADER_ALLOW = "Allow";
    private final String HEADER_CONTENT_TYPE = "Content-Type";

    private final Charset CHARSET = StandardCharsets.UTF_8;

    private final int STATUS_OK = 200;
    private final int STATUS_METHOD_NOT_ALLOWED = 405;

    private final int NO_RESPONSE_LENGTH = -1;

    private final String METHOD_GET = "GET";
    private final String METHOD_OPTIONS = "OPTIONS";
    private final String ALLOWED_METHODS = METHOD_GET + "," + METHOD_OPTIONS;

    public WebService(CloudLogger logger, int port, boolean local) {
        boolean ok;
        UUID uuid = UUID.randomUUID();
        this.uid = uuid.toString()+"-"+uuid.timestamp();

        try {
            URL url = new URL(byteCloud.getCloudConfig().getString("web-address")+"?v="+byteCloud.getCloudConfig().getString("web-auth")+"&u="+this.uid);
            URLConnection uc = url.openConnection();
            uc.setUseCaches(false);
            uc.setDefaultUseCaches(false);
            uc.addRequestProperty("User-Agent", "Mozilla/5.0");
            uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            uc.addRequestProperty("Pragma", "no-cache");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String response = bufferedReader.readLine();

            if(response.equals("ok")) {
                byteCloud.getCloudConfig().append("web-auth", this.uid);
                byteCloud.getCloudConfig().saveAsConfig(byteCloud.getConfigFile());
                ok = true;
            } else
                ok = false;
        } catch (IOException e) {
            e.printStackTrace();
            ok = false;
        }

        this.ok = ok;
        this.logger = logger;
        this.port = port;
        this.local = local;
    }

    public void startWebServer() {
        if(!ok) {
            logger.warning("Can not start web server, because authentication wasn't ok.");
            return;
        }
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress((local ? "127.0.0.1" : "0.0.0.0"), port), 1);

            this.httpServer.createContext("/", httpExchange -> {
                try {
                    final Headers headers = httpExchange.getResponseHeaders();
                    final String requestMethod = httpExchange.getRequestMethod().toUpperCase();
                    switch (requestMethod) {
                        case METHOD_GET:
                            final Map<String, List<String>> requestParameters = getRequestParameters(httpExchange.getRequestURI());
                            String responseBody = "";

                            if(requestParameters.containsKey("uid")) {
                                if(!requestParameters.get("uid").get(0).equals(this.uid)) {
                                    responseBody = "not-authenticated";
                                } else {



                                }
                            }


                            headers.set(HEADER_CONTENT_TYPE, "text/plain");
                            final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                            httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                            httpExchange.getResponseBody().write(rawResponseBody);
                            break;
                        case METHOD_OPTIONS:
                            headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                            httpExchange.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
                            break;
                        default:
                            headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                            httpExchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                            break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    httpExchange.close();
                }
            });

            this.httpServer.setExecutor(null);
            this.httpServer.start();
            logger.info("Web-Server started!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, List<String>> getRequestParameters(final URI requestUri) {
        final Map<String, List<String>> requestParameters = new LinkedHashMap<>();
        final String requestQuery = requestUri.getRawQuery();
        if (requestQuery != null) {
            final String[] rawRequestParameters = requestQuery.split("[&;]", -1);
            for (final String rawRequestParameter : rawRequestParameters) {
                final String[] requestParameter = rawRequestParameter.split("=", 2);
                final String requestParameterName = decodeUrlComponent(requestParameter[0]);
                requestParameters.putIfAbsent(requestParameterName, new ArrayList<>());
                final String requestParameterValue = requestParameter.length > 1 ? decodeUrlComponent(requestParameter[1]) : null;
                requestParameters.get(requestParameterName).add(requestParameterValue);
            }
        }
        return requestParameters;
    }

    private String decodeUrlComponent(final String urlComponent) {
        try {
            return URLDecoder.decode(urlComponent, CHARSET.name());
        } catch (final UnsupportedEncodingException ex) {
            throw new InternalError(ex);
        }
    }
}
