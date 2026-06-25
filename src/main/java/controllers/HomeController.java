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
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    HomeService homeService = HomeService.getInstance();
    PaymentService paymentService;
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<OrderItem> orderItems;
    OrderItem selectedItem;

    @FXML private ScrollPane itemScrollPane;
    @FXML private FlowPane itemContainer;

    @FXML private ScrollPane orderScrollPane;
    @FXML private VBox orderContainer;

    @FXML private Label orderTotalLabel;
    @FXML private Label orderPaidLabel;

    // init. This is in charge of calling the required functions to ensure the GUI is loaded correctly
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeService.init();
        items.add(new Item(1, "Cheeseburger", new BigDecimal("8.50"), 1, new ArrayList<>(List.of("No Cheese", "No Pickles"))));
        items.add(new Item(2, "Double Burger", new BigDecimal("11.00"), 1, new ArrayList<>(List.of("No Cheese", "Extra Patty"))));
        items.add(new Item(3, "Chicken Burger", new BigDecimal("9.50"), 1, new ArrayList<>(List.of("No Mayo", "Extra Sauce"))));
        items.add(new Item(4, "Veggie Burger", new BigDecimal("9.00"), 1, new ArrayList<>()));
        items.add(new Item(5, "Hot Dog", new BigDecimal("6.50"), 1, new ArrayList<>(List.of("No Mustard", "No Onion"))));
        items.add(new Item(6, "Small Fries", new BigDecimal("3.50"), 1, new ArrayList<>()));
        items.add(new Item(7, "Large Fries", new BigDecimal("5.00"), 1, new ArrayList<>()));
        items.add(new Item(8, "Onion Rings", new BigDecimal("4.50"), 1, new ArrayList<>()));
        items.add(new Item(9, "Chicken Nuggets 6pc", new BigDecimal("7.00"), 6, new ArrayList<>(List.of("BBQ Sauce", "Sweet Chilli"))));
        items.add(new Item(10, "Chicken Nuggets 12pc", new BigDecimal("12.00"), 12, new ArrayList<>(List.of("BBQ Sauce", "Sweet Chilli"))));
        items.add(new Item(11, "Caesar Salad", new BigDecimal("10.50"), 1, new ArrayList<>(List.of("No Croutons", "No Dressing"))));
        items.add(new Item(12, "Coleslaw", new BigDecimal("3.00"), 1, new ArrayList<>()));
        items.add(new Item(13, "Soft Drink Small", new BigDecimal("2.50"), 1, new ArrayList<>(List.of("Coke", "Sprite", "Fanta"))));
        items.add(new Item(14, "Soft Drink Large", new BigDecimal("4.00"), 1, new ArrayList<>(List.of("Coke", "Sprite", "Fanta"))));
        items.add(new Item(15, "Milkshake", new BigDecimal("6.50"), 1, new ArrayList<>(List.of("Chocolate", "Vanilla", "Strawberry"))));
        items.add(new Item(16, "Orange Juice", new BigDecimal("4.50"), 1, new ArrayList<>()));
        items.add(new Item(17, "Water", new BigDecimal("2.00"), 1, new ArrayList<>()));
        items.add(new Item(18, "Coffee", new BigDecimal("4.50"), 1, new ArrayList<>(List.of("Almond Milk", "Oat Milk"))));
        items.add(new Item(19, "Fish Burger", new BigDecimal("9.50"), 1, new ArrayList<>(List.of("No Tartar", "Extra Lemon"))));
        items.add(new Item(20, "BLT Wrap", new BigDecimal("10.00"), 1, new ArrayList<>(List.of("No Bacon", "Extra Lettuce"))));
        items.add(new Item(21, "Pulled Pork Burger", new BigDecimal("13.00"), 1, new ArrayList<>(List.of("No Slaw", "Extra Sauce"))));
        items.add(new Item(22, "Corn on the Cob", new BigDecimal("3.50"), 1, new ArrayList<>(List.of("No Butter"))));
        items.add(new Item(23, "Mac and Cheese", new BigDecimal("5.50"), 1, new ArrayList<>()));
        items.add(new Item(24, "Loaded Fries", new BigDecimal("8.00"), 1, new ArrayList<>(List.of("No Cheese", "No Bacon"))));
        items.add(new Item(25, "Pizza Slice", new BigDecimal("5.00"), 1, new ArrayList<>(List.of("Pepperoni", "Margherita", "BBQ Chicken"))));
        items.add(new Item(26, "Hotcakes", new BigDecimal("7.00"), 3, new ArrayList<>(List.of("No Syrup", "Extra Butter"))));
        items.add(new Item(27, "Hash Brown", new BigDecimal("2.50"), 1, new ArrayList<>()));
        items.add(new Item(28, "Breakfast Wrap", new BigDecimal("9.00"), 1, new ArrayList<>(List.of("No Egg", "No Bacon"))));
        items.add(new Item(29, "Ice Cream Cup", new BigDecimal("3.50"), 1, new ArrayList<>(List.of("Chocolate", "Vanilla"))));
        items.add(new Item(30, "Apple Pie", new BigDecimal("4.00"), 1, new ArrayList<>()));


        renderItems();
        renderOrderItems();
    }

    // Render items on the GUI
    private void renderItems() {
        // FIXME: Breaks if items is null
        itemContainer.getChildren().clear();

        // Use a for loop to create a button for each item in the items ArrayList.
        for (Item item : items) {
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

