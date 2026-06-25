package models;
import java.util.ArrayList;
import models.Item;
import models.OrderItem;

public class Order {
    String orderName;
    int orderIndex;
    ArrayList<OrderItem> orderItems = new ArrayList<>();
    Boolean orderCompleted = false;


    // Constructor
    public Order(int idx, String name) {
        if (name == "") {
            name = "No Name Provided";
        }
        orderName = name;
        orderIndex = idx;
    }

    // Add an item to the order
    public void addItem(Item item) {
        OrderItem newItem = new OrderItem(item.getName(),orderItems.size(), item.getDefaultQty(), new ArrayList<>(), item.getItemPrice());
        orderItems.add(newItem);
    }

    // Void/Remove item from the order
    public void voidItem(OrderItem item) {
        System.out.println("Order.voidItem(), Item details: "+item.getItemName());
        for (int i = 0; i < orderItems.size(); i++) {
            System.out.println("Checking item at index: " + i);
            if (orderItems.get(i).getItemId() == item.getItemId()) {
                System.out.println("Item found, removing!");
                orderItems.remove(i);
            }
        }
    }

    // Getters:


    // Get the items ArrayList of the order
    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public Boolean getOrderCompleted() {
        return orderCompleted;
    }


    public int getOrderIndex() {
        return orderIndex;
    }

    // Return a string representation of the order
    public String getOrderRepr() {
        String returnString = orderName + "\n";
        for (int i = 0; i < orderItems.size(); i++) {
            returnString = returnString + orderItems.get(i).getItemId() + "\n";
        }
        return returnString;
    }

    // Setters:

    // Set the items qty by the ID of the item.
    // TODO: Can this be faster than O(n)??
    public void setItemQuantityById(int id, int quantity) {
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getItemId() == id) {
                orderItems.set(quantity, orderItems.get(i));
            }
        }
    }
    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public void setOrderComplete(Boolean to) {
        orderCompleted = to;
    }
    }