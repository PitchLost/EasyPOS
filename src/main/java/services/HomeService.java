package services;
import models.Item;
import models.Order;
import models.OrderItem;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Central service for managing orders during a session.
 * Holds the in-memory list of orders and tracks which one is currently active.
 * <p>
 * Implemented as a singleton since order state needs to persist across
 * the lifetime of the application without being re-fetched constantly.
 */
public class HomeService {
    Boolean debugMode = true;
    BigDecimal total = new BigDecimal("0.00");

    /** All orders created in the current session. */
    public ArrayList<Order> orders = new ArrayList<>();
    ArrayList<Order> cachedOrders = new ArrayList<>();

    CacheService caching = new CacheService();

    /** Index into {@code orders} pointing to the currently active order. */
    int orderIndex;

    // Singleton logic. We need this since HomeService also stores the memory version of orders. (It's still cached but that shouldnt be
    // constantly grabbed) Another approach is to create an OrderService but I think that would also be just as complicated as this.
    private static HomeService instance;

    /**
     * Returns the shared instance, creating it if it doesn't exist yet.
     */
    public static HomeService getInstance() {
        if (instance == null) {
            instance = new HomeService();
        }
        return instance;
    }

    /**
     * Sets up the service on first launch. If no orders exist yet, creates one
     * and marks it as active. Safe to call more than once — does nothing if
     * orders are already present.
     */
    public void init() {
        orders = caching.loadOrders();
        if (!orders.isEmpty()) {
            return;
        }
        newOrder();
        selectOrder(orders.get(0));
        saveOrders(); // Orders updated, update the save (Even if it is just 1)
    }

    /**
     * Adds an item to whichever order is currently active.
     *
     * @param item the item to add
     */
    public void addItem(Item item) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.addItem(item);
        saveOrders(); // Orders updated, update the save
    }

    /**
     * Removes an item from the active order and adjusts the running total accordingly.
     *
     * @param item the order item to void
     */
    public void voidItem(OrderItem item) {
        Order currentOrder = orders.get(orderIndex);
        System.out.println("HomeService.voidItem()");
        currentOrder.voidItem(item);
        incrementTotal(new BigDecimal("0.00").subtract(item.getItemPrice()));
        saveOrders(); // Orders updated, update the save
    }

    /**
     * Returns all items belonging to the active order.
     */
    public ArrayList<OrderItem> getCurrentOrderItems() {
        Order currentOrder = orders.get(orderIndex);
        return currentOrder.getOrderItems();
    }

    /**
     * Creates a new order, appends it to the session list, and makes it active.
     */
    public void newOrder() {
        // Create new order and add it to the orders ArrayList
        Order newOrder = new Order(orders.size(), "No Name");
        orders.add(newOrder);

        // TODO: Cache order
        selectOrder(newOrder);
    }

    /**
     * Switches the active order to the one provided.
     *
     * @param order the order to make active (Order type)
     */
    public void selectOrder(Order order) {
        orderIndex = order.getOrderIndex();
        // TODO: Send some sort of UI update
    }

    /**
     * Updates the quantity of a specific item in the active order.
     *
     * @param item the order item (As an OrderItem type) whose quantity should change
     * @param qty  the new quantity (Int)
     */
    void editQty(OrderItem item, int qty) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.setItemQuantityById(item.getItemId(), qty);
        saveOrders(); // Orders updated, update the save
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
     * @param total the amount (As a BigDecimal) to add (or subtract if negative in the case of void item)
     */
    public void incrementTotal(BigDecimal total) {
        this.total = this.total.add(total);
    } // TODO: Add this to the order??

    /**
     * Returns the most recently added item in the active order, or {@code null}
     * if the order is empty.
     */
    public OrderItem getTopItem() {
        ArrayList<OrderItem> items = getCurrentOrderItems();
        if (items.isEmpty()) {
            return null;
        }
        return items.get(items.size() - 1);
    }

    // CACHING:

    /** Uses {@link services.CacheService} to save the orders ArrayList
     * */
    public void saveOrders() {
        caching.saveOrders(orders);
    }
}
