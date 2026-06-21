package models;
import java.math.BigDecimal;
import java.util.ArrayList;

public class Item {
    int itemId;
    String itemName;
    BigDecimal itemPrice;
    int defaultQty = 1;
    ArrayList<String> itemOptions = new ArrayList<>();

    public Item(int itemId, String itemName, BigDecimal itemPrice, int defaultQty, ArrayList<String> itemOptions) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.defaultQty = defaultQty;
        this.itemOptions = itemOptions;
    }



    public String getName() {
        return "";
    }

    public int getId() {
        return itemId;
    }
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
}
