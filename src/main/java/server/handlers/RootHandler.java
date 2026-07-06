package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CopyOnWriteArrayList;

public class RootHandler extends BaseHandler {
    // Thread-safe list to hold open connections
    private final CopyOnWriteArrayList<HttpExchange> connectedDevices = new CopyOnWriteArrayList<>();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Handle standard request methods if needed, but filter for SSE updates
        if ("GET".equals(exchange.getRequestMethod())) {
            setupSSEConnection(exchange);
        } else {
            sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
        }
    }

    private void setupSSEConnection(HttpExchange exchange) throws IOException {
        // 1. Set SSE specific headers to keep the connection open
        exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.getResponseHeaders().set("Connection", "keep-alive");

        // 2. Send 200 OK status code but chunked/infinite content length (0 means chunked)
        exchange.sendResponseHeaders(200, 0);

        // 3. Store this exchange in your list to keep track of the device
        connectedDevices.add(exchange);

        // Note: DO NOT call exchange.close() here. Leaving it open allows streaming data.
    }

    public void broadcastUpdate(String message) {
        // Format required by Server-Sent Events standard
        String ssePayload = "data: " + message + "\n\n";
        byte[] dataBytes = ssePayload.getBytes();

        for (HttpExchange exchange : connectedDevices) {
            try {
                OutputStream os = exchange.getResponseBody();
                os.write(dataBytes);
                os.flush(); // Force transmission over the wire
            } catch (IOException e) {
                // Connection was likely closed by the client device
                System.out.println("Device disconnected. Removing from registry.");
                try { exchange.close(); } catch (Exception ignored) {}
                connectedDevices.remove(exchange);
            }
        }
    }

}
