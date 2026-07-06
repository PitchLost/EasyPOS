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
        Environment env = caching.loadEnv();
        env.setHost(true);
        env.setOrdersMode(false);
        env.setServerPort(port);
        caching.saveEnv(env);
        networkManager.startHostMode(port);
    }

    public void enterOrdersMode(int port) {
        Environment env = caching.loadEnv();
        env.setServerPort(port);
        env.setHost(false);
        env.setOrdersMode(true);
        caching.saveEnv(env);
        networkManager.switchToClientMode(port);
    }

    public void saveServerAddress(String ip, int port) {
        Environment env = caching.loadEnv();
        env.setServerAddress(ip);
        env.setServerPort(port);
        caching.saveEnv(env);
    }

    public Environment getEnv() {
        return caching.loadEnv();
    }
}
