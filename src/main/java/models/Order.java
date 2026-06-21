package models;
import java.util.ArrayList;
import models.Item;
import models.OrderItem;

public class Order {
    String orderName;
    int orderIndex;
    ArrayList<OrderItem> orderItems = new ArrayList<>();


    // Constructor
    public Order(int idx, String name) {
        orderName = name;
        orderIndex = idx;
    }

    // Add an item to the order
    public void addItem(Item item) {
        OrderItem newItem = new OrderItem(item.getName(),orderItems.size(), item.getDefaultQty(), new ArrayList<>(), item.getItemPrice());
        orderItems.add(newItem);
    }

    // Void/Remove item from the order
    public int voidItem(Item item) {
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getItemId() == item.getId()) {
                orderItems.remove(i);
                return 1;
            }
        }
        return 0;
    }

    // Getter for the items ArrayList of the order
    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

//    public OrderItem getItemFromId(int id) {
//        for (int i = 0; i < orderItems.size(); i++) {
//            if (orderItems.get(i).getItemId() == id) {
//            return orderItems.get(i);
//            }
//        }
//        return 0;
//        }

     // TODO: Can this be a bit shorter and cleaner?
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
    }