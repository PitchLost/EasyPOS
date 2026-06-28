package models;
import java.math.BigDecimal;

import java.util.ArrayList;

// The class for an item added to an order. It mirrors a normal item but has a few extra fields that the order needs.
// TODO: Inherit from order?
public class OrderItem {
    String itemName;
    int itemId;
    int itemQuantity;
    ArrayList<String> itemOptions = new ArrayList<>();
    BigDecimal itemPrice; // The price of the item*qty
    BigDecimal unitPrice; // The original price of the item

    // Constructor. Can pass an empty ArrayList for options if none
    public OrderItem(String name, int id, int quantity, ArrayList<String> options, BigDecimal price) {
        itemName = name;
        itemId = id;
        itemQuantity = quantity;
        itemOptions = options;
        itemPrice = price;
        unitPrice = price;
    }

    // Getter for item quantity
    public int getItemQuantity() {
        return itemQuantity;
    }

    // Sets item quantity to a set int amount
    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    // Returns the item "status" as a string. For example 1x item no cheese
    public String getItemDetails() {
        String options = itemOptions.isEmpty() ? "" : " (" + String.join(", ", itemOptions) + ")";
        return String.format("%dx %s%s", itemQuantity, itemName, options);
    }


    // Getter for item id
    public int getItemId() {
        return itemId;
    }

    // Getter for item price
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemName() {
        return itemName;
    }
}
