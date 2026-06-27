package services;

import models.Item;
import models.Order;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

/**
 * Handles reading and writing persistent data to disk.
 * All app data is stored as JSON under ~/.easypos/
 * This is the only class that should directly access those files.
 */
public class CacheService {
    private static final String DATA_DIR = System.getProperty("user.home") + "/.easypos/"; // Edit this if you want to change the save directory
    private static final String MENU_FILE = DATA_DIR + "menu.json";
    private static final String ORDERS_FILE = DATA_DIR + "orders.json";
    private static final String OLD_ORDERS_FILE = DATA_DIR + "old_orders.json";
    private static final String CATEGORIES_FILE = DATA_DIR + "categories.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Creates the ~/.easypos/ directory if it doesn't already exist.
     */
    private void ensureDataDirExists() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));
    }

    // MENU:

    /**
     * Serializes the menu and writes it to disk.
     */
    public void saveMenu(LinkedHashMap<String, ArrayList<Item>> menu) {
        try {
            ensureDataDirExists();
            String json = gson.toJson(menu);
            Files.writeString(Paths.get(MENU_FILE), json);
            System.out.println("Menu has been saved! :)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the menu from disk and deserializes it back into a LinkedHashMap. Returns an empty map if no file exists yet.
     */
    public LinkedHashMap<String, ArrayList<Item>> loadMenu() {
        try {
            if (!Files.exists(Paths.get(MENU_FILE)))
                return new LinkedHashMap<>(); // Return empty hashmap if not exists yet
            String json = Files.readString(Paths.get(MENU_FILE));
            Type type = new TypeToken<LinkedHashMap<String, ArrayList<Item>>>() {
            }.getType(); // Deserialize back into a LinkedHashMap
            return gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new LinkedHashMap<>(); // Maybe change to a delibrate crash instead?
        }
    }

    /**
     * Updates an item in the menu
     *
     * @param updatedItem The item to be updated as an {@link Item} object
     */
    public void updateItem(Item updatedItem) {
        LinkedHashMap<String, ArrayList<Item>> menu = loadMenu();

        for (ArrayList<Item> items : menu.values()) {
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getItemName().equals(updatedItem.getItemName())) {
                    items.set(i, updatedItem);
                    saveMenu(menu);
                    return;
                }
            }
        }
    }

    /**
     * Deletes an item from the menu
     *
     * @param item The item to be deleted as an {@link Item} object
     */
    public void deleteItem(Item item) {
        LinkedHashMap<String, ArrayList<Item>> menu = loadMenu();

        for (ArrayList<Item> items : menu.values()) {
            if (items.removeIf(i -> i.getItemName().equals(item.getItemName()))) {
                saveMenu(menu);
                return;
            }
        }
    }

    /**
     * Adds an item to the menu
     *
     * @param item The item to be added as a complete {@link Item} object
     */
    public void addItem(Item item) {
        LinkedHashMap<String, ArrayList<Item>> menu = loadMenu();

        menu.computeIfAbsent(item.getItemCategory(), k -> new ArrayList<>()).add(item);
        saveMenu(menu);
    }

    // ORDERS:

    /**
     * Serializes the orders list and writes it to disk.
     */
    public void saveOrders(ArrayList<Order> orders) {
        try {
            ensureDataDirExists();
            String json = gson.toJson(orders);
            Files.writeString(Paths.get(ORDERS_FILE), json);
            System.out.println("Orders have been saved! :)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads orders from disk and deserializes them back into an ArrayList. Returns an empty list if no file exists yet.
     */
    public ArrayList<Order> loadOrders() {
        try {
            if (!Files.exists(Paths.get(ORDERS_FILE))) return new ArrayList<>();
            String json = Files.readString(Paths.get(ORDERS_FILE));
            Type type = new TypeToken<ArrayList<Order>>() {
            }.getType();
            return gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveOldOrders(ArrayList<Order> oldOrders) {
        try {
            ensureDataDirExists();
            String json = gson.toJson(oldOrders);
            Files.writeString(Paths.get(OLD_ORDERS_FILE), json);
            System.out.println("Old Orders have been saved! :)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Order> loadOldOrders() {
        try {
            if (!Files.exists(Paths.get(OLD_ORDERS_FILE))) return new ArrayList<>();
            String json = Files.readString(Paths.get(OLD_ORDERS_FILE));
            Type type = new TypeToken<ArrayList<Order>>() {
            }.getType();
            return gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void addNewCompletedOrder(Order completedOrder) {
        ArrayList<Order> oldOrders = loadOldOrders();
        oldOrders.add(completedOrder);
        saveOldOrders(oldOrders);
    }

    // CATEGORIES:

    public void saveCategories(ArrayList<String> categories) {
        try {
            ensureDataDirExists();
            String json = gson.toJson(categories);
            Files.writeString(Paths.get(CATEGORIES_FILE), json);
            System.out.println("Categories have been saved! :)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> loadCategories() {
        try {
            if (!Files.exists(Paths.get(CATEGORIES_FILE))) return new ArrayList<>();
            String json = Files.readString(Paths.get(CATEGORIES_FILE));
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            return gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Renames a category in the categories list.
     *
     * @param from the current category name as a {@link String}
     * @param to   the new category name as a {@link String}
     */
    public void updateCategory(String from, String to) {
        ArrayList<String> categories = loadCategories();
        int index = categories.indexOf(from);
        if (index == -1) return;

        categories.set(index, to);
        saveCategories(categories);
    }


    /**
     * Deletes a category from the categories list
     *
     * @param category The category to be deleted as a {@link String}
     */
    public void deleteCategory(String category) {
        ArrayList<String> categories = loadCategories();
        categories.removeIf(c -> c.equals(category));
        saveCategories(categories);
    }

    /**
     * Adds a new category to the categories list.
     *
     * @param category the name of the category to add as a {@link String}
     */
    public void addCategory(String category) {
        ArrayList<String> categories = loadCategories();
        if (categories.contains(category)) return;
        categories.add(category);
        saveCategories(categories);
    }

}