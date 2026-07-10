package server;

import com.sun.net.httpserver.HttpServer;
import server.handlers.OrderHandler;
import server.handlers.RootHandler;
import services.CacheService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private final HttpServer httpServer;

    public Server(int port, CacheService cacheService) throws IOException {
        // Use 0.0.0.0 so other devices on the Wi-Fi network can connect. Localhost does not work
        this.httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        this.httpServer.createContext("/", new RootHandler());
        this.httpServer.createContext("/orders", new OrderHandler(cacheService));
        this.httpServer.setExecutor(null);
    }

    /** Starts the server */
    public void start() {
        this.httpServer.start();
        System.out.println("Local server started: address:" + httpServer.getAddress());
    }

    /**
     * Safely stops the server.
     * @param delaySeconds How many seconds to wait for active requests to finish before forcing close.
     */
    public void stop(int delaySeconds) {
        if (this.httpServer != null) {
            this.httpServer.stop(delaySeconds);
            System.out.println("Local server stopped successfully.");
        }
    }
}
