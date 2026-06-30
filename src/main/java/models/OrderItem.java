package models;
import java.math.BigDecimal;

import java.util.ArrayList;

/** The class for an item added to an order. It mirrors a normal item but has a few extra fields that the order needs.*/
// TODO: Implement item?
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

    // Getters/Setters
    public int getItemQuantity() {return itemQuantity;}
    public void setItemQuantity(int itemQuantity) {this.itemQuantity = itemQuantity;}
    public int getItemId() {return itemId;}
    public BigDecimal getUnitPrice() {return unitPrice;}
    public BigDecimal getItemPrice() {return itemPrice;}
    public void setItemPrice(BigDecimal itemPrice) {this.itemPrice = itemPrice;}
    public String getItemName() {return itemName;}
}
