package controllers;

import javafx.scene.layout.FlowPane;
import models.Item;
import models.OrderItem;
import services.HomeService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    HomeService homeService = new HomeService();
    ArrayList<Item> items;

    @FXML private ScrollPane itemScrollPane;
    @FXML private FlowPane itemContainer;

    @FXML private ScrollPane orderScrollPane;
    @FXML private VBox orderContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeService.newOrder();
        runTests();
        renderItems();
        renderOrderItems();
    }
    // Render items on the GUI
    private void renderItems() {
        itemContainer.getChildren().clear();

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

    private void renderOrderItems() {
        orderContainer.getChildren().clear();

        for (OrderItem item : homeService.getCurrentOrderItems()) {
            Label pane = new Label(item.getItemName() + " x" + item.getItemQuantity() + "  $" + item.getItemPrice());
            pane.setStyle("-fx-border-color: grey; -fx-padding: 8;");
            pane.setMaxWidth(Double.MAX_VALUE);
            orderContainer.getChildren().add(pane);
        }
    }

    // --- Handlers ---

    private void handleItemClicked(Item item) {
        homeService.addItem(item);
        renderOrderItems();
    }

    // FIXME: Delete this when caching implemented
    void runTests() {
        items = new ArrayList<>();
        items.add(new Item(1, "Apple",        new BigDecimal("2.00"),      1, new ArrayList<>()));
        items.add(new Item(2, "Banana",       new BigDecimal("1.50"),      1, new ArrayList<>()));
        items.add(new Item(3, "Pear",         new BigDecimal("100000.00"), 1, new ArrayList<>()));
        items.add(new Item(4, "Grapefruit",   new BigDecimal("0.00"),      1, new ArrayList<>()));
        items.add(new Item(5, "Watermelon",   new BigDecimal("1.00"),      2, new ArrayList<>()));
        items.add(new Item(6, "Passionfruit", new BigDecimal("0.00"),      2, new ArrayList<>()));
    }
}

    // TODO:
    // Render a button for each available item from the cache
    // Create a pane for each item in the currently selected order
    // Listen and handle calls from the actions buttons

