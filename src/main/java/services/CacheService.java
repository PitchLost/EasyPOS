package services;

import models.Environment;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles reading and writing persistent data to disk.
 * All app data is stored as JSON under ~/.easypos/
 * This is the only class that should directly access those files. It provides helpers for other classes to use to load/save these files
 */
public class CacheService {
    private static final String DATA_DIR = System.getProperty("user.home") + "/.easypos/"; // Edit this if you want to change the save directory
    private static final String MENU_FILE = DATA_DIR + "menu.json";
    private static final String ORDERS_FILE = DATA_DIR + "orders.json";
    private static final String OLD_ORDERS_FILE = DATA_DIR + "old_orders.json";
    private static final String CATEGORIES_FILE = DATA_DIR + "categories.json";
    private static final String MONEY_BUTTONS_FILE = DATA_DIR + "money_buttons.json";
    private static final String ENVIRONMENT_FILE = DATA_DIR + "environment.json";


    // A simple one lined delceration of GSON turned into this mess to handle the timestamping
    private final Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

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

    private static final int MAX_OLD_ORDERS = 100; // TODO: Make this user configurable in settings

    public void addNewCompletedOrder(Order completedOrder) {
        ArrayList<Order> oldOrders = loadOldOrders();
        oldOrders.add(completedOrder);
        if (oldOrders.size() > MAX_OLD_ORDERS) {
            oldOrders.subList(0, oldOrders.size() - MAX_OLD_ORDERS).clear();
        }
        saveOldOrders(oldOrders);
    }
    // TODO: Add some sort of large order archive that stores every previous order
    // CATEGORIES:

    /** Saves the categories to the disk as an ArrayList
     * @param categories The categories to save*/

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

    /** Loads the categories ArrayList from the disk
     * @return Saved Categories*/
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

    /** Saves the MoneyButtons to the disk
     * @param buttons The MoneyButtons to save */
    public void saveMoneyButtons(MoneyButtons buttons) {
        try {
            ensureDataDirExists();
            Files.writeString(Paths.get(MONEY_BUTTONS_FILE), gson.toJson(buttons));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Loads the MoneyButtons from disk
     * @return The MoneyButtons saved on the disk*/
    public MoneyButtons loadMoneyButtons() {
        try {
            if (!Files.exists(Paths.get(MONEY_BUTTONS_FILE))) return null;
            String json = Files.readString(Paths.get(MONEY_BUTTONS_FILE));
            return gson.fromJson(json, MoneyButtons.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Adds a new button to either the coin or note list.
     * @param type either "COIN" or "NOTE"
     * @param value the value to add as a {@link String} e.g. "0.50" or "20" */
    public void addMoneyButton(String type, String value) {
        MoneyButtons buttons = loadMoneyButtons();
        if (buttons == null) buttons = new MoneyButtons(new ArrayList<>(), new ArrayList<>());

        if (type.equals("COIN")) {
            if (!buttons.getCoinButtons().contains(value)) buttons.getCoinButtons().add(value);
        } else if (type.equals("NOTE")) {
            if (!buttons.getNoteButtons().contains(value)) buttons.getNoteButtons().add(value);
        }

        saveMoneyButtons(buttons);
    }

    /** Removes a button from either the coin or note list.
     * @param type either "COIN" or "NOTE"
     * @param value the value to remove as a {@link String} */
    public void deleteMoneyButton(String type, String value) {
        MoneyButtons buttons = loadMoneyButtons();
        if (buttons == null) return;

        if (type.equals("COIN")) {
            buttons.getCoinButtons().remove(value);
        } else if (type.equals("NOTE")) {
            buttons.getNoteButtons().remove(value);
        }

        saveMoneyButtons(buttons);
    }

    /**
     * Saves a given {@link Environment} object to the disk for future use.
     * @param env Enviroment object to save
     */
    public void saveEnv(Environment env) {
        try {
            ensureDataDirExists();
            String json = gson.toJson(env);
            Files.writeString(Paths.get(ENVIRONMENT_FILE), json);
            System.out.println("Environment has been saved! :)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads and returns the saved enviroment from disk.
     * @return {@link Environment} object either pulled from disk or a new default one
     */
    public Environment loadEnv() {
        try {
            if (!Files.exists(Paths.get(ENVIRONMENT_FILE))) return new Environment(false, false, 8000);
            String json = Files.readString(Paths.get(ENVIRONMENT_FILE));
            return gson.fromJson(json, Environment.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Environment(false, false, 8000);
        }
    }
}