package controllers;

import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Item;
import models.Order;
import models.OrderItem;
import services.CacheService;
import services.HomeService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import services.PaymentService;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

/** The controller class for the Home screen. Works with HomeService to render everything on the home screen and has all the logic for creating new orders, adding items
 * to the order etc. No {@link HomeController} functions are called from {@link HomeService}*/

public class HomeController implements Initializable {
    HomeService homeService = HomeService.getInstance();
    PaymentService paymentService;
    CacheService caching = new CacheService();
    LinkedHashMap<String, ArrayList<Item>> menu = new LinkedHashMap<>();
    ArrayList<String> categories = new ArrayList<>();
    String selectedCategory;
    OrderItem selectedItem;

    // FXML ELEMENTS
    @FXML private ScrollPane itemScrollPane;
    @FXML private FlowPane itemContainer;
    @FXML private VBox orderContainer;
    @FXML private Label orderTotalLabel;
    @FXML private HBox categoryContainer;
    @FXML private Label OrderNameLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get menu and categories from cache
        loadMenu();
        loadCategories();


        // Exit if menu is empty. No items have been created yet so there's nothing to render
        if (menu.isEmpty()) {
            return;
        }
        renderCategories();
        renderItems();
        renderOrderItems();
    }


    // Render the category buttons
    private void renderCategories() {
        categoryContainer.getChildren().clear();
        if (categories.isEmpty()) return; // No categories to load

        selectedCategory = categories.get(0); // Just select the first one

        for (String category : categories) {
            Button btn = new Button(category);
            btn.setPrefWidth(120);
            btn.setPrefHeight(40);
            btn.setStyle(
                    "-fx-background-color: #1a1a2e;" +
                            "-fx-text-fill: #aaaaaa;" +
                            "-fx-font-size: 13;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: #444466;" +
                            "-fx-border-radius: 6;" +
                            "-fx-border-width: 1;"
            );
            btn.setOnAction(e -> handleCategoryClicked(category, btn));
            categoryContainer.getChildren().add(btn); // TODO: Hightlight the first button
        }
    }

    private void handleCategoryClicked(String category, Button btn) {
        selectedCategory = category;

        // Reset all button styles, then highlight the selected one
        for (var node : categoryContainer.getChildren()) {
            node.setStyle(
                    "-fx-background-color: #1a1a2e;" +
                            "-fx-text-fill: #aaaaaa;" +
                            "-fx-font-size: 13;" +
                            "-fx-background-radius: 6;" +
                            "-fx-border-color: #444466;" +
                            "-fx-border-radius: 6;" +
                            "-fx-border-width: 1;"
            );
        }
        // Set the selected buttons styles
        btn.setStyle(
                "-fx-background-color: #2f12b3;" +
                        "-fx-text-fill: #ffffff;" +
                        "-fx-font-size: 13;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6;" +
                        "-fx-border-color: #e8a020;" +
                        "-fx-border-radius: 6;" +
                        "-fx-border-width: 1;"
        );
        renderItems();
    }


    // Render items on the GUI
    private void renderItems() {
        itemContainer.getChildren().clear();
        ArrayList<Item> items = menu.get(selectedCategory); // Get the items from the menu map for the selectedCategory
        if (items == null) return; // No items to load

        // Create button for each item
        for (Item item : items) {
            Button btn = new Button(item.getName() + "\n$" + item.getItemPrice());
            btn.setPrefWidth(120);
            btn.setPrefHeight(50);
            btn.setStyle(
                    "-fx-background-color: #157cbd;" +
                            "-fx-text-fill: #ffffff;" +
                            "-fx-font-size: 12;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #333333;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-width: 1;" +
                            "-fx-alignment: center;"
            );
            btn.setOnAction(e -> handleItemClicked(item));
            itemContainer.getChildren().add(btn);
        }
    }

    // Renders the items in the order onto the GUI
    private void renderOrderItems() {
        homeService.recalculateTotal(); // Ensure the total has been calculated since loading the orders from cache
        orderTotalLabel.setText(convertToMoney(homeService.getTotal()));
        OrderNameLabel.setText(homeService.getActiveOrder().getOrderName());
        orderContainer.getChildren().clear();

        // Create a clickable pane for each orderItem
        for (OrderItem item : homeService.getCurrentOrderItems()) {
            Label pane = new Label(item.getItemName() + " x" + item.getItemQuantity() + "  $" + item.getItemPrice());
            pane.setStyle("-fx-border-color: grey; -fx-padding: 8;");
            pane.setMaxWidth(Double.MAX_VALUE);

            // When a pane is clicked (Selected)
            pane.setOnMouseClicked(e -> {
                // Reset all labels to default style
                for (var node : orderContainer.getChildren()) {
                    node.setStyle("-fx-border-color: grey; -fx-padding: 8;");
                }
                // Highlight selected
                pane.setStyle("-fx-border-color: #e8a020; -fx-padding: 8; -fx-background-color: #1a2a3a;");
                selectedItem = item;
            });
            orderContainer.getChildren().add(pane);
        }
    }

    // FXML HANDLERS

    @FXML
    private void handleItemClicked(Item item) {
        homeService.addItem(item);
        selectedItem = homeService.getTopItem();
        homeService.incrementTotal(item.getItemPrice());
        renderOrderItems();
    }


    @FXML
    public void handleNewOrder() {
        homeService.newOrder();
        renderOrderItems();
    }


    @FXML
    public void handleToPaymentClicked() {
        paymentService = new PaymentService(homeService.getTotal(), homeService.getActiveOrder());
        Stage stage = (Stage) itemScrollPane.getScene().getWindow();

        NavigationController.navigateTo(stage, "/FXML/payment.fxml", controller -> {
            PaymentController c = (PaymentController) controller;
            c.setPaymentService(paymentService);
        });
    }


    @FXML
    public void handleToOrdersClicked() {
        Stage stage = (Stage) itemScrollPane.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/orders.fxml");
    }


    @FXML
    public void handleEditName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Order Name");
        dialog.setHeaderText("Edit Name for \"" + homeService.getActiveOrder().getOrderName() + "\"");
        dialog.setContentText("New Name:");
        dialog.initOwner(itemScrollPane.getScene().getWindow());

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.isBlank()) {
                try {
                    homeService.editOrderName(newName);
                    selectedItem = null;
                    renderOrderItems();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid name"); // TODO: Show an actual error
                }
            }
        });
    }


    @FXML
    public void handleEditQty() {
        if (selectedItem == null) return;

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedItem.getItemQuantity()));
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText("Edit quantity for \"" + selectedItem.getItemName() + "\"");
        dialog.setContentText("New quantity:");
        dialog.initOwner(itemScrollPane.getScene().getWindow());

        dialog.showAndWait().ifPresent(newQty -> {
            if (!newQty.isBlank()) {
                try {
                    int qty = Integer.parseInt(newQty);
                    homeService.editQty(selectedItem, qty);
                    selectedItem = null;
                    renderOrderItems();
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity"); // TODO: Show an actual error
                }
            }
        });
    }


    @FXML
    public void handleVoidItem() {
        if (selectedItem == null) {
            selectedItem = homeService.getTopItem();
        }
        homeService.voidItem(selectedItem);
        selectedItem = homeService.getTopItem();
        renderOrderItems();
    }


    @FXML
    public void handleVoidOrder() {
        Order activeOrder = homeService.getActiveOrder();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Void Order");
        alert.setHeaderText("Void \"" + activeOrder.getOrderName() + "\"?");
        alert.setContentText("This will delete the order!");
        alert.initOwner(itemContainer.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                homeService.removeOrder(homeService.getActiveOrder());
                handleToOrdersClicked();
                renderOrderItems();
            }
        });
    }


    @FXML
    public void handleToSettingsClicked() {
        Stage stage = (Stage) itemScrollPane.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/settings.fxml");
    }

    // HELPERS:

    // Take a BigDecimal and covert to a string
    private String convertToMoney(BigDecimal amount) {
        return "$"+amount.toPlainString();
    }

    // Load from caching
    private void loadMenu() {
        menu = caching.loadMenu();
    }
    private void loadCategories() {
        categories = caching.loadCategories();
    }

}

