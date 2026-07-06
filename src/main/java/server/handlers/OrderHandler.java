package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import models.Order;
import services.CacheService;
import services.HomeService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrderHandler extends BaseHandler {
    private final CacheService cacheService;
    HomeService homeService = HomeService.getInstance();

    // Replicate the exact Gson setup here that CahceService has so timestamps serialize correctly
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new services.LocalDateTimeAdapter())
            .create();

    // The constructor forces the server to give this handler the CacheService instance
    public OrderHandler(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        switch (exchange.getRequestMethod()) {
            case "GET" -> handleGet(exchange);
            default -> sendResponse(exchange, 405, "{\"error\": \"Method not allowed\"}", "application/json");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        try {
            ArrayList<Order> orders = cacheService.loadOrders();
            sendResponse(exchange, 200, gson.toJson(orders), "application/json");
        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "{\"error\": \"Failed to retrieve orders\"}", "application/json");
        }
    }


}
