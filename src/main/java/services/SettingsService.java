package services;

import models.Environment;
import server.NetworkManager;

public class SettingsService {
    CacheService caching = new CacheService();
    NetworkManager networkManager = NetworkManager.getInstance();


    public void resetServerSettings() {
        caching.saveEnv(new Environment(false, false, 8000));
        networkManager.killServer();
    }

    public void enterHostMode(int port) {
        caching.saveEnv(new Environment(true, false, port));
        networkManager.startHostMode(port);
    }

    public void enterOrdersMode(int port) {
        caching.saveEnv(new Environment(false, true, port));
        networkManager.switchToClientMode(port);
    }

    public void saveServerAddress(String ip, int port) {
        Environment env = caching.loadEnv();
        env.setServerAddress(ip);
        env.setServerPort(port);
        caching.saveEnv(env);
    }
}
