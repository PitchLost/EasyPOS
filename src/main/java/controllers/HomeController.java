package controllers;

import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import models.Item;
import models.OrderItem;
import services.HomeService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import services.PaymentService;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    HomeService homeService = HomeService.getInstance();
    PaymentService paymentService;
    LinkedHashMap<String, ArrayList<Item>> menu = new LinkedHashMap<>();
    String selectedCategory;
    OrderItem selectedItem;

    @FXML private ScrollPane itemScrollPane;
    @FXML private FlowPane itemContainer;
    @FXML private ScrollPane orderScrollPane;
    @FXML private VBox orderContainer;
    @FXML private Label orderTotalLabel;
    @FXML private Label orderPaidLabel;
    @FXML private FlowPane categoryContainer;

    // init. This is in charge of calling the required functions to ensure the GUI is loaded correctly
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeService.init();
        loadMenu();
        if (menu.isEmpty()) {
            return;
        }
        renderCategories();
        renderItems();
        renderOrderItems();
    }


    private void loadMenu() {
        // TODO: Get the hashmap from the cache
    }


    private void renderCategories() {
        selectedCategory = menu.keySet().iterator().next();
        for (String category : menu.keySet()) {
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
            categoryContainer.getChildren().add(btn);
        }
    }

    private void handleCategoryClicked(String category, Button btn) {
        selectedCategory = category;

        // Reset all tab styles, then highlight the selected one
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

        btn.setStyle(
                "-fx-background-color: #e8a020;" +
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
        // FIXME: Breaks if items is null
        itemContainer.getChildren().clear();

        // Use a for loop to create a button for each item in the items ArrayList.
        for (Item item : menu.get(selectedCategory)) {
            Button btn = new Button(item.getName() + "\n$" + item.getItemPrice());
            btn.setPrefWidth(200);
            btn.setPrefHeight(80);
            btn.setStyle(
                    "-fx-background-color: #2a2a2a;" +
                            "-fx-text-fill: #ffffff;" +
                            "-fx-font-size: 15;" +
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
        orderTotalLabel.setText(convertToMoney(homeService.getTotal()));
        orderContainer.getChildren().clear();

        for (OrderItem item : homeService.getCurrentOrderItems()) {
            Label pane = new Label(item.getItemName() + " x" + item.getItemQuantity() + "  $" + item.getItemPrice());
            pane.setStyle("-fx-border-color: grey; -fx-padding: 8;");
            pane.setMaxWidth(Double.MAX_VALUE);
            orderContainer.getChildren().add(pane);
        }
    }

    // Handlers

    // Handle when an item is clicked (Added to the order from the bottom part of the GUI)
    private void handleItemClicked(Item item) {
        homeService.addItem(item);
        selectedItem = homeService.getTopItem();
        homeService.incrementTotal(item.getItemPrice());
        renderOrderItems();
    }

    // Handle the action button "Checkout Order"
    public void handleToPaymentClicked() {
        paymentService = new PaymentService(homeService.getTotal(), homeService.getActiveOrder());
        Stage stage = (Stage) itemScrollPane.getScene().getWindow(); // Get the active stage object from a loaded FXML element

        NavigationController.navigateTo(stage, "/FXML/payment.fxml", controller -> {
            PaymentController c = (PaymentController) controller;
            c.setPaymentService(paymentService);
        });
    }

    public void handleToSettingsClicked() {
        Stage stage = (Stage) itemScrollPane.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/settings.fxml");
    }

    public void handleVoidItem() {
        homeService.voidItem(selectedItem);
        selectedItem = homeService.getTopItem();
        renderOrderItems();
    }

    // Helpers:
    private String convertToMoney(BigDecimal amount) {
        return "$"+amount.toPlainString();
    }

}

    // TODO:
    // Listen and handle calls from the actions buttons
    // The buttons look a little funky, they need a little work

