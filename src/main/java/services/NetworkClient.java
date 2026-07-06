package services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class NetworkClient {
    private static NetworkClient instance;
    private CompletableFuture<Void> activeStreamFuture = null;
    private boolean shouldListen = false;

    private NetworkClient() {}

    public static synchronized NetworkClient getInstance() {
        if (instance == null) {
            instance = new NetworkClient();
        }
        return instance;
    }

    /**
     * Spawns an asynchronous background stream thread targeting the Host server.
     */
    public synchronized void startListening(String baseUrl) {
        stopListening(); // Safety: Kill any lingering loops first
        shouldListen = true;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/")) // Leave a trailing /
                .header("Accept", "text/event-stream")
                .GET()
                .build();

        System.out.println("NetworkClient: Opening real-time stream to " + baseUrl);

        activeStreamFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenAccept(response -> {
                    response.body().forEach(line -> {
                        // Halt instantly if the user disabled client mode mid-stream
                        if (!shouldListen) return;

                        if (line.startsWith("data: ")) {
                            String jsonPayload = line.substring(6).trim();
                            // Forward payload directly to HomeService data context
                            HomeService.getInstance().handleServerUpdate(jsonPayload);
                        }
                    });
                })
                .exceptionally(ex -> {
                    if (shouldListen) {
                        System.err.println("NetworkClient: Connection dropped. Reconnecting in 5s...");
                        try { Thread.sleep(5000); } catch (InterruptedException ignored) {}
                        synchronized(this) {
                            if (shouldListen) startListening(baseUrl);
                        }
                    }
                    return null;
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
