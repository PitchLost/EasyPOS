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
    public ArrayList<Order> orders = new ArrayList<>();
    CacheService caching = new CacheService();
    int orderIndex;
    private static HomeService instance;
    private Runnable onOrdersUpdated = null;


    public static HomeService getInstance() {
        if (instance == null) {
            instance = new HomeService();
        }
        return instance;
    }
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
        saveOrders();
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

    /** Updates the orders list from an external source (e.g. server) without triggering a sync back. */
    public void setOrdersFromServer(ArrayList<Order> incomingOrders) {
        this.orders = incomingOrders;
        saveOrdersLocal();
    }

    /** Saves orders locally without syncing to server — used when receiving from server to avoid loop. */
    public void saveOrdersLocal() {
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
        if (orders.isEmpty()) {
            newOrder();
            selectOrder(orders.get(0));
            orderIndex = 0;
        }
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
        // No more syncToServer() — host is the only one that mutates orders
    }

    /** Receives updated orders from the host server and updates local state. */
    public synchronized void updateOrdersFromHost(ArrayList<Order> incomingOrders) {
        this.orders = incomingOrders;
        if (orderIndex >= orders.size()) orderIndex = 0;
        if (!orders.isEmpty()) recalculateTotal();

        // Notify the UI on the JavaFX thread
        if (onOrdersUpdated != null) {
            javafx.application.Platform.runLater(onOrdersUpdated);
        }
    }

    public void setOnOrdersUpdated(Runnable callback) {
        this.onOrdersUpdated = callback;
    }
}
