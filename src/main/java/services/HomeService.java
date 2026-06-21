package services;
import models.Item;
import models.Order;
import models.OrderItem;
import java.util.ArrayList;

public class HomeService {
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
    // Edit name
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
        // TODO: Send some sort of UI update
    }

    void editName(String name) {
        debugPrint("Edit Name");
        Order selectedOrder = orders.get(orderIndex);
        selectedOrder.setOrderName(name);
        // TODO: Send a UI update and update the name element
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



}

