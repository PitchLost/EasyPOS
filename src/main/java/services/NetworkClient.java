package services;

import models.Environment;
import models.Order;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class NetworkClient {
    private static NetworkClient instance;
    private CompletableFuture<Void> activeStreamFuture = null;
    private boolean shouldListen = false;

    private NetworkClient() {}

    /** Returns the singleton version of the NetworkClient */
    public static synchronized NetworkClient getInstance() {
        if (instance == null) {
            instance = new NetworkClient();
        }
        return instance;
    }

    /**
     * Creates an asynchronous background stream thread targeting the Host server.
     */
    public synchronized void startListening(String baseUrl) {
        stopListening();
        shouldListen = true;

        System.out.println("NetworkClient: Starting poll loop to " + baseUrl);

        activeStreamFuture = CompletableFuture.runAsync(() -> {
            HttpClient client = HttpClient.newHttpClient();
            while (shouldListen) {
                try {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(baseUrl + "/orders"))
                            .GET()
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() == 200) {
                        com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                                .registerTypeAdapter(java.time.LocalDateTime.class, new LocalDateTimeAdapter())
                                .create();
                        java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<ArrayList<Order>>(){}.getType();
                        ArrayList<Order> orders = gson.fromJson(response.body(), listType);
                        HomeService.getInstance().updateOrdersFromHost(orders);
                        System.out.println("NetworkClient: Orders updated from host.");
                    }

                    Thread.sleep(2000); // Poll every 2 seconds

                } catch (Exception ex) {
                    if (shouldListen) {
                        System.err.println("NetworkClient: Poll failed, retrying in 5s...");
                        try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                    }
                }
            }
        });
    }

    /**
     * Tears down the active network streaming channel cleanly.
     */
    public synchronized void stopListening() {
        shouldListen = false;
        if (activeStreamFuture != null) {
            activeStreamFuture.cancel(true);
            activeStreamFuture = null;
            System.out.println("NetworkClient: Background stream stopped.");
        }
    }
}
