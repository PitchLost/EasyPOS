package models;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Item {
    int itemId;
    String itemName;
    BigDecimal itemPrice;
    String itemCategory;
    int defaultQty = 1;
    ArrayList<String> itemOptions = new ArrayList<>();

    public Item(String itemName, String itemCategory, BigDecimal itemPrice, int defaultQty, ArrayList<String> itemOptions) {
        this.itemName = itemName;
        this.itemCategory = itemCategory;
        this.itemPrice = itemPrice;
        this.defaultQty = defaultQty;
        this.itemOptions = itemOptions;
    }

    public String getName() {
        return itemName;
    }
    public void setName(String name) {this.itemName = name;}
    public String getItemName() {
        return itemName;
    }
    public BigDecimal getItemPrice() {
        return itemPrice;
    }
    public int getDefaultQty() {
        return defaultQty;
    }
    public ArrayList<String> getItemOptions() {
        return itemOptions;
    }
    public String getItemCategory() {
        return itemCategory;
    }
    public void setItemCategory(String category) {
        this.itemCategory = category;
    }
    public void setItemPrice(BigDecimal price) {
        this.itemPrice = price;
    }
    public void setDefaultQty(int qty) {
        this.defaultQty = qty;
    }
}
