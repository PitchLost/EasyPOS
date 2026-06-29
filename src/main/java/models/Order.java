package models;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import models.Item;
import models.OrderItem;


public class Order {
    String orderName;
    int orderIndex;
    UUID orderId;
    ArrayList<OrderItem> orderItems = new ArrayList<>();
    Boolean orderCompleted = false;


    // Constructor
    public Order(int idx, String name) {
        if (name.isEmpty()) {
            name = "Order " + idx;
        }
        orderName = name;
        orderId =  UUID.randomUUID();
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

    public void setOrderComplete(Boolean to) {
        orderCompleted = to;
    }


    public int getOrderIndex() {
        return orderIndex;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public BigDecimal getOrderTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            total = total.add(item.getItemPrice());
        }
        return total;
    }

    // Set the items qty by the ID of the item.
    public void setItemQuantityById(int id, int quantity) {
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getItemId() == id) {
                OrderItem orderItem = orderItems.get(i);
                orderItem.setItemQuantity(quantity);
                orderItem.setItemPrice(orderItem.getUnitPrice().multiply(new BigDecimal(quantity)));

                return;
            }
        }
    }
    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }


    }