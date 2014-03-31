package com.softmotions.ncms;

import ninja.utils.NinjaConstant;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaPropertiesImpl;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Adamansky Anton (adamansky@gmail.com)
 */
public class NcmsTestServer {

    private final int port;
    private final URI serverUri;
    private final NcmsJetty ncmsJetty;

    public NcmsTestServer() {
        this.port = findAvailablePort(1000, 10000);
        serverUri = createServerUri();

        ncmsJetty = new NcmsJetty();
        ncmsJetty.setPort(this.port);
        ncmsJetty.setServerUri(serverUri);
        ncmsJetty.setNinjaMode(NinjaMode.test);
        ncmsJetty.start();

    }

    public Injector getInjector() {
        return ncmsJetty.getInjector();
    }

    public String getServerAddress() {
        return serverUri.toString() + "/";
    }

    public URI getServerAddressAsUri() {
        return serverUri;
    }

    private URI createServerUri() {
        try {
            return new URIBuilder().setScheme("http").setHost("localhost")
                    .setPort(port).build();
        } catch (URISyntaxException e) {
            // should not be able to happen...
            return null;
        }
    }

    public void shutdown() {
        ncmsJetty.shutdown();
    }

    private static int findAvailablePort(int min, int max) {
        for (int port = min; port < max; port++) {
            try {
                new ServerSocket(port).close();
                return port;
            } catch (IOException e) {
                // Must already be taken
            }
        }
        throw new IllegalStateException(
                "Could not find available port in range " + min + " to " + max);
    }


    public static class NcmsJetty {

        static final int DEFAULT_PORT = 8080;

        int port;

        URI serverUri;

        NinjaMode ninjaMode;

        Server server;

        ServletContextHandler context;

        String ninjaContextPath;

        NcmsServletListener ninjaServletListener;

        public NcmsJetty() {
            //some sensible defaults
            port = DEFAULT_PORT;
            serverUri = URI.create("http://localhost:" + port);
            ninjaMode = NinjaMode.dev;
        }

        public Injector getInjector() {
            return ninjaServletListener.getInjector();
        }

        public NcmsJetty setPort(int port) {
            this.port = port;
            return this;
        }

        public NcmsJetty setServerUri(URI serverUri) {
            this.serverUri = serverUri;
            return this;
        }

        public NcmsJetty setNinjaMode(NinjaMode ninjaMode) {
            this.ninjaMode = ninjaMode;
            return this;
        }

        public NcmsJetty setNinjaContextPath(String ninjaContextPath) {
            this.ninjaContextPath = ninjaContextPath;
            return this;
        }

        public void start() {
            server = new Server(port);
            try {

                ServerConnector http = new ServerConnector(server);
                server.addConnector(http);
                context = new ServletContextHandler(server, ninjaContextPath);

                NinjaPropertiesImpl ninjaProperties
                        = new NinjaPropertiesImpl(ninjaMode);
                // We are using an embeded jetty for quick server testing. The
                // problem is that the port will change.
                // Therefore we inject the server name here:
                ninjaProperties.setProperty(NinjaConstant.serverName, serverUri.toString());

                ninjaServletListener = new NcmsServletListener(ninjaProperties);
                context.addEventListener(ninjaServletListener);
                context.addFilter(GuiceFilter.class, "/*", null);
                context.addServlet(DefaultServlet.class, "/");

                server.start();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        public void shutdown() {
            try {
                server.stop();
                server.destroy();
                context.stop();
                context.destroy();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String getServerAddress() {
            return serverUri.toString() + "/";
        }

        public URI getServerAddressAsUri() {
            return serverUri;
        }
    }

}
