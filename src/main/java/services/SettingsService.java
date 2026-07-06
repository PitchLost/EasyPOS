package services;

import models.Environment;
import server.NetworkManager;

public class SettingsService {
    CacheService caching = new CacheService();
    NetworkManager networkManager = NetworkManager.getInstance();


    /** Resets the server config and stops the server if its running */
    public void resetServerSettings() {
        caching.saveEnv(new Environment(false, false, 8000));
        networkManager.killServer();
    }

    /** Resets and closes the app, deleting all cached data too */
    public void resetApp() {
        try {
            java.nio.file.Path dataDir = java.nio.file.Paths.get(System.getProperty("user.home") + "/.easypos/");
            if (java.nio.file.Files.exists(dataDir)) {
                java.nio.file.Files.walk(dataDir)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(java.nio.file.Path::toFile)
                        .forEach(java.io.File::delete);
            }
            System.out.println("App data deleted.");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    /** Starts the server and enters host mode */
    public void enterHostMode(int port) {
        Environment env = caching.loadEnv();
        env.setHost(true);
        env.setOrdersMode(false);
        env.setServerPort(port);
        caching.saveEnv(env);
        networkManager.startHostMode(port);
    }

    /** Connects to another server and enters orders mode */
    public void enterOrdersMode(int port) {
        Environment env = caching.loadEnv();
        env.setServerPort(port);
        env.setHost(false);
        env.setOrdersMode(true);
        caching.saveEnv(env);
        networkManager.switchToClientMode(port);
    }

    /** Saves the server address */
    public void saveServerAddress(String ip, int port) {
        Environment env = caching.loadEnv();
        env.setServerAddress(ip);
        env.setServerPort(port);
        caching.saveEnv(env);
    }

    /** Gets the enviroment from cache */
    public Environment getEnv() {
        return caching.loadEnv();
    }
}
