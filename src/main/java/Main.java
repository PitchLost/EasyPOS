import java.util.ArrayList;
import java.util.Scanner;

import models.*;

public class Main {
    Boolean debugMode = true;


    public ArrayList<Order> orders = new ArrayList<>();
    ArrayList<Order> cachedOrders = new ArrayList<>();
    int orderIndex;



    public void init() {
        // TODO:
        // Get cached orders and update orders array
    }

    void debugPrint(String message) {
        if (debugMode) {
            System.out.println(message);
        }
    }

    // Main add item to order, Calls the addItem method for the relvant order object
    void addItem(Item item) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.addItem(item);
    }

    // Main void item from order, calls the voidItem method for the relevant oder object
    void voidItem(Item item) {
        Order currentOrder = orders.get(orderIndex);
        currentOrder.voidItem(item);
    }

    //TODO:
    // New order
    // Edit name
    // Select order
    // Edit Quantity
    // Go to settings
    // Pay for order

    void newOrder() {
        debugPrint("New Order");
        // Create new order and add it to the orders ArrayList
        Order newOrder = new Order(orders.size(), "No Name");
        orders.add(newOrder);

        // TODO: Cache order

        debugPrint("Order has been created");
        selectOrder(newOrder);
    }

    void selectOrder(Order order) {
        debugPrint("Select Order");
        orderIndex = order.getOrderIndex();
    }

    void editQty(Item item, int qty) {
        debugPrint("Edit Qty");
        Order currentOrder = orders.get(orderIndex);
        OrderItem selectedItem = currentOrder.getItemFromId();
        selectedItem.setItemQuantity(qty);
    }

    void toSettings() {
        debugPrint("To Settings");
    }

    void toPay() {
        debugPrint("Payment menu");
    }



}

