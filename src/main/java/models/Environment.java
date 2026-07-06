package models;

public class Environment {
    boolean isHost;
    boolean ordersMode;
    int serverPort = 8000;
    String serverAddress;

    public Environment(boolean isHost, boolean ordersMode, int serverPort) {
        this.isHost = isHost;
        this.ordersMode = ordersMode;
        this.serverPort = serverPort;
        this.serverAddress = "127.0.0.1";
    }

    public boolean isHost() { return isHost; }
    public boolean isOrdersMode() { return ordersMode; }
    public int getServerPort() { return serverPort; }
    public String getServerAddress() { return serverAddress; }
}