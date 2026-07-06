package services;

import models.Item;
import models.Order;
import models.OrderItem;
import models.Environment;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Central service for managing orders during a session.
 * Holds the in-memory list of orders and tracks which one is currently active.
 * Implemented as a singleton since order state needs to persist across
 * the lifetime of the application without being re-fetched constantly.
 */
public class HomeService {
    BigDecimal total = new BigDecimal("0.00");

    /**
     * All active orders created in the current session or fetched from the cache
     */
    public ArrayList<Order> orders = new ArrayList<>();

    /**
     * A {@link CacheService} object which provides the methods of saving/loading persistent data
     */
    CacheService caching = new CacheService();

    /**
     * A pointer into the orders array which points to the active order object.
     */
    int orderIndex;

    /**
     * Shared {@link HomeService} object. This is only set once by the {@link HomeService#getInstance()} function
     */
    private static HomeService instance;

    /**
     * Returns the shared HomeService instance, creating it if it doesn't exist yet.
     */
    public static HomeService getInstance() {
        if (instance == null) {
            instance = new HomeService();
        }
        return instance;
    }

    /**
     * Sets up the service on first launch. If no orders exist yet, creates one and marks it as active.
     */
    public void init() {
        orders = caching.loadOrders();
        if (!orders.isEmpty()) {
            return;
        }
        newOrder();
        selectOrder(orders.get(0));
        saveOrders();
    }

    /**
     * Adds an item to whichever order is currently active.
     *
     * @param item the item to add as a {@link Item}
     */
    public void addItem(Item item) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.addItem(item);
        saveOrders();
    }

    /**
     * Removes an item from the active order and adjusts the running total accordingly.
     *
     * @param item the order item to void as an {@link OrderItem}
     */
    public void voidItem(OrderItem item) {
        Order currentOrder = orders.get(orderIndex);
        System.out.println("HomeService.voidItem()");
        currentOrder.voidItem(item);
        incrementTotal(new BigDecimal("0.00").subtract(item.getItemPrice()));
        saveOrders();
    }

    /**
     * Returns all items belonging to the active order.
     */
    public ArrayList<OrderItem> getCurrentOrderItems() {
        Order currentOrder = orders.get(orderIndex);
        return currentOrder.getOrderItems();
    }

    /**
     * Creates a new order, appends it to the active orders array, and makes it active.
     */
    public void newOrder() {
        Order newOrder = new Order(orders.size(), "");
        orders.add(newOrder);
        caching.saveOrders(orders);
        selectOrder(newOrder);
    }

    /**
     * Adds a new order by passing an Order object
     *
     * @param order The order to add to the existing orders
     */
    public void addOrder(Order order) {
        System.out.println("An order has been recieved from the server");
        orders.add(order);
        caching.saveOrders(orders);
    }

    /**
     * Removes an order from orders and caches it.
     *
     * @param order the order to remove as an {@link Order} object
     */
    public void removeOrder(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                caching.addNewCompletedOrder(orders.get(i));
                orders.remove(i);
            }
        }
        if (!orders.isEmpty()) {
            orderIndex = 0;
            selectOrder(orders.get(0));
        } else {
            newOrder();
        }
        saveOrders();
    }

    /**
     * Switches the active order to the one provided.
     *
     * @param order the order to make active as an {@link Order}
     */
    public void selectOrder(Order order) {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(order.getOrderId())) {
                orderIndex = i;
                total = orders.get(orderIndex).getOrderTotal();
                recalculateTotal();
                return;
            }
        }
    }

    /**
     * Recalculates the running total from scratch using the active order's items.
     */
    public void recalculateTotal() {
        total = BigDecimal.ZERO;
        for (OrderItem item : getActiveOrder().getOrderItems()) {
            total = total.add(item.getItemPrice());
        }
    }

    /**
     * Updates the quantity of a specific item in the active order.
     *
     * @param item the order item whose quantity should change as an {@link OrderItem}
     * @param qty  the new quantity as an int
     */
    public void editQty(OrderItem item, int qty) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.setItemQuantityById(item.getItemId(), qty);
        saveOrders();
        recalculateTotal();
    }

    /**
     * Updates the name of the selected order
     *
     * @param name the new name as a {@link String}
     */
    public void editOrderName(String name) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.setOrderName(name);
        saveOrders();
    }

    /**
     * Returns the current running total across all additions and voids.
     */
    public BigDecimal getTotal() {
        return this.total;
    }

    /**
     * Returns the currently active order object.
     */
    public Order getActiveOrder() {
        return orders.get(orderIndex);
    }

    /**
     * Adds {@code total} to the running total. Pass a negative value to subtract.
     *
     * @param total the amount as a {@link BigDecimal} to add (or subtract if negative in the case of void item)
     */
    public void incrementTotal(BigDecimal total) {
        this.total = this.total.add(total);
    }

    /**
     * Returns the most recently added item in the active order, or {@code null} if the order is empty.
     */
    public OrderItem getTopItem() {
        ArrayList<OrderItem> items = getCurrentOrderItems();
        if (items.isEmpty()) {
            return null;
        }
        return items.get(items.size() - 1);
    }

    /**
     * Returns all active orders.
     */
    public ArrayList<Order> getOrders() {
        return orders;
    }

    /**
     * Returns old orders from caching
     */
    public ArrayList<Order> getOldOrders() {
        return caching.loadOldOrders();
    }

    /**
     * Creates a new active order recovered from an old order, named "Recovered - {originalName}".
     */
    public Order recoverOrder(Order oldOrder) {
        Order recovered = new Order(orders.size(), "Recovered - " + oldOrder.getOrderName());
        for (OrderItem item : oldOrder.getOrderItems()) {
            recovered.getOrderItems().add(item);
        }
        recovered.setOrderComplete(true);
        orders.add(recovered);
        selectOrder(recovered);
        saveOrders();
        return recovered;
    }

    /**
     * Uses {@link CacheService} to save the orders ArrayList.
     */
    public void saveOrders() {
        caching.saveOrders(orders);
        syncToServer();
    }

    /**
     * Sends the current orders to the host server if running in client mode.
     */
    private void syncToServer() {
        Environment env = caching.loadEnv();
        if (env.isHost()) return; // host doesn't need to POST to itself

        try {
            String json = new com.google.gson.GsonBuilder().registerTypeAdapter(java.time.LocalDateTime.class, new services.LocalDateTimeAdapter()).create().toJson(orders);
            String address = "http://localhost:" + env.getServerPort() + "/orders";
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(address))
                    .header("Content-Type", "application/json")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(json))
                    .build();
            System.out.println("Request sent to address: " + address);

            // FIXED: Completed your cut-off HttpClient response handler chain
            client.sendAsync(request, java.net.http.HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() != 200) {
                            System.err.println("Server rejected sync request. Status code: " + response.statusCode());
                        }
                    })
                    .exceptionally(ex -> {
                        System.err.println("Failed to sync structural changes to server: " + ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            System.err.println("Error constructing network payload: " + e.getMessage());
        }
    }

    /**
     * Gateway for NetworkClient to pass live, incoming backend server pushes directly into memory.
     */
    public synchronized void handleServerUpdate(String json) {
        try {
            com.google.gson.Gson gson = new com.google.gson.GsonBuilder()
                    .registerTypeAdapter(java.time.LocalDateTime.class, new services.LocalDateTimeAdapter())
                    .create();

            java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<ArrayList<Order>>() {
            }.getType();
            ArrayList<Order> updatedOrders = gson.fromJson(json, listType);

            if (updatedOrders != null) {
                this.orders = updatedOrders;
                caching.saveOrders(this.orders);

                // Safety bound check in case the server altered or cleared array structures
                if (this.orderIndex >= this.orders.size()) {
                    this.orderIndex = 0;
                }
                if (!this.orders.isEmpty()) {
                    recalculateTotal();
                }
            }
        } catch (Exception e) {
            System.err.println("Error mapping server push serialization: " + e.getMessage());
        }
    }
}
