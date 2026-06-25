package services;
import models.Item;
import models.Order;
import models.OrderItem;

import java.math.BigDecimal;
import java.util.ArrayList;

public class HomeService {
    Boolean debugMode = true;
    BigDecimal total = new BigDecimal("0.00");
    public ArrayList<Order> orders = new ArrayList<>();
    ArrayList<Order> cachedOrders = new ArrayList<>();
    int orderIndex;

    // Singleton logic. We need this since HomeService also stores the memory version of orders. (It's still cached but that shouldnt be
    // constantly grabbed) Another approach is to create an OrderService but I think that would also be just as complicated as this.
    private static HomeService instance;

    public static HomeService getInstance() {
        if (instance == null) {
            instance = new HomeService();
        }
        return instance;
    }

    public void init() {
        if (!orders.isEmpty()) {
            return;
        }
        newOrder();
        selectOrder(orders.get(0));
    }



    //TODO:
    // Go to settings
    // Pay for order

    // Main add item to order, Calls the addItem method for the relevant order object
    public void addItem(Item item) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.addItem(item);
    }

    // Main void item from order, calls the voidItem method for the relevant order object
    public void voidItem(OrderItem item) {

        Order currentOrder = orders.get(orderIndex);
        System.out.println("HomeService.voidItem()");
        currentOrder.voidItem(item);
        incrementTotal(new BigDecimal("0.00").subtract(item.getItemPrice()));
    }

    public ArrayList<OrderItem> getCurrentOrderItems() {
        Order currentOrder = orders.get(orderIndex);
        return currentOrder.getOrderItems();
    }


    public void newOrder() {
        // Create new order and add it to the orders ArrayList
        Order newOrder = new Order(orders.size(), "No Name");
        orders.add(newOrder);

        // TODO: Cache order
        selectOrder(newOrder);
    }

    public void selectOrder(Order order) {
        orderIndex = order.getOrderIndex();
        // TODO: Send some sort of UI update
    }


    void editQty(OrderItem item, int qty) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.setItemQuantityById(item.getItemId(), qty);
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public Order getActiveOrder() {
        return orders.get(orderIndex);
    }

    public void incrementTotal(BigDecimal total) {
        this.total = this.total.add(total);
    }

    public OrderItem getTopItem() {
        ArrayList<OrderItem> items = getCurrentOrderItems();
        if (items.isEmpty()) {
            return null;
        }
        return items.get(items.size() - 1);
    }
}

