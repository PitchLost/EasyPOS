package server;

import server.Server;
import services.CacheService;
import services.HomeService;

import java.util.ArrayList;

public class NetworkManager {
    private static NetworkManager instance;
    private HomeService homeService = HomeService.getInstance();

    private final CacheService cacheService;
    private Server localServerInstance;
    private boolean isHostMode = false;
    private String targetServerIp = "localhost";

    private NetworkManager() {
        this.cacheService = new CacheService();
    }

    /**
     * Singleton instance accessor.
     */
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    /**
     * Call this when the app starts, or when the user switches this device to 'Host Mode'.
     */
    public synchronized void startHostMode(int port) {
        // Stop listening as a client if this machine is becoming the host
        services.NetworkClient.getInstance().stopListening();

        if (isHostMode && localServerInstance != null) {
            System.out.println("Server is already running.");
            return;
        }

        try {
            // Boot the server
            localServerInstance = new Server(port, cacheService);
            localServerInstance.start();

            isHostMode = true;
            targetServerIp = "localhost";
            System.out.println("App is now running in HOST mode.");
        } catch (Exception e) {
            System.err.println("Failed to start host mode: " + e.getMessage());
        }
    }

    /**
     * Call this when the user wants to switch this terminal to look at another central machine.
     */
    public synchronized void switchToClientMode(int centralServerPort) {
        String centralServerIp = cacheService.loadEnv().getServerAddress();
        String fullUrl = "http://" + centralServerIp + ":" + cacheService.loadEnv().getServerPort();
        homeService.updateOrdersFromHost(new ArrayList<>());

        System.out.println("Switching to client mode. Connecting to: " + fullUrl);

        if (localServerInstance != null) {
            localServerInstance.stop(0);
            localServerInstance = null;
        }

        isHostMode = false;
        targetServerIp = fullUrl;

        System.out.println("App is now running in CLIENT mode.");
        services.NetworkClient.getInstance().startListening(fullUrl);
    }

    /**
     * Shuts down any local server and drops background client connections.
     */
    public synchronized void killServer() {
        // Terminate client loops if they are running
        services.NetworkClient.getInstance().stopListening();

        if (isHostMode && localServerInstance != null) {
            localServerInstance.stop(0);
            localServerInstance = null;
        }
        isHostMode = false;
    }

    /**
     * Returns the active target network endpoint.
     */
    public String getTargetServerIp() {
        return this.targetServerIp;
    }
}
