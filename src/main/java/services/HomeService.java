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
        // TODO:
        // Get cached orders and update orders array

        // If no orders exist already create an order and select it
        newOrder();
        selectOrder(orders.get(0));
        total = new BigDecimal("0.00");
    }

    void debugPrint(String message) {
        if (debugMode) {
            System.out.println(message);
        }
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
    public void voidItem(Item item) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.voidItem(item);
    }

    public ArrayList<OrderItem> getCurrentOrderItems() {
        Order currentOrder = orders.get(orderIndex);
        return currentOrder.getOrderItems();
    }


    public void newOrder() {
        debugPrint("New Order");
        // Create new order and add it to the orders ArrayList
        Order newOrder = new Order(orders.size(), "No Name");
        orders.add(newOrder);

        // TODO: Cache order

        debugPrint("Order has been created");
        selectOrder(newOrder);
    }

    public void selectOrder(Order order) {
        debugPrint("Select Order");
        orderIndex = order.getOrderIndex();
        // TODO: Send some sort of UI update
    }

    void editName(String name) {
        debugPrint("Edit Name");
        Order selectedOrder = orders.get(orderIndex);
        selectedOrder.setOrderName(name);
        // TODO: Send a UI update and update a name element
    }

    // Edits the quantity by calling an order method to set qty through an items id
    // This is a method that will be called from the UI, It can only be called correctly because if the item doesnt exist than there is no way for it to have been called
    void editQty(OrderItem item, int qty) {
        debugPrint("Edit Qty");
        Order currentOrder = orders.get(orderIndex);
        currentOrder.setItemQuantityById(item.getItemId(), qty);
    }
    void toSettings() {
        debugPrint("To Settings");
    }

    void toPay() {
        debugPrint("Payment menu");
    }


    public BigDecimal getTotal() {
        System.out.println("Total has been requested: " + this.total);
        return this.total;
    }

    public void incrementTotal(BigDecimal total) {
        this.total = this.total.add(total);
        System.out.println("New total is: " + this.total);
    }
}

