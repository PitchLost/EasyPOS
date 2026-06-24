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
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    BigDecimal total;

    HomeService homeService = new HomeService();
    PaymentService paymentService;
    ArrayList<Item> items = new ArrayList<>();
    ArrayList<OrderItem> orderItems;

    @FXML private ScrollPane itemScrollPane;
    @FXML private FlowPane itemContainer;

    @FXML private ScrollPane orderScrollPane;
    @FXML private VBox orderContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        homeService.newOrder();
        items.add(new Item(1, "Apple", new BigDecimal("1.00"), 1, new ArrayList<>()));
        items.add(new Item(2, "Pear", new BigDecimal("1.50"), 1, new ArrayList<>()));
        items.add(new Item(3, "Banna", new BigDecimal("2.00"), 1, new ArrayList<>()));
        items.add(new Item(4, "Kiwifruit", new BigDecimal("0.00"), 2, new ArrayList<>()));
        items.add(new Item(5, "Melon", new BigDecimal("100.00"), 2, new ArrayList<>()));


        renderItems();
        renderOrderItems();
    }
    // Render items on the GUI
    private void renderItems() {
        // FIXME: Breaks if items is null
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

    // Renders the items in the order
    private void renderOrderItems() {
        orderContainer.getChildren().clear();

        for (OrderItem item : homeService.getCurrentOrderItems()) {
            Label pane = new Label(item.getItemName() + " x" + item.getItemQuantity() + "  $" + item.getItemPrice());
            pane.setStyle("-fx-border-color: grey; -fx-padding: 8;");
            pane.setMaxWidth(Double.MAX_VALUE);
            orderContainer.getChildren().add(pane);
        }
    }

    // Handlers

    private void handleItemClicked(Item item) {
        homeService.addItem(item);
        homeService.incrementTotal(item.getItemPrice());
        renderOrderItems();
    }

    public void handleToPaymentClicked() {
        paymentService = new PaymentService(homeService.getTotal(), homeService.getCurrentOrderItems());
        Stage stage = (Stage) itemScrollPane.getScene().getWindow(); // Get the active stage object from a loaded FXML element

        NavigationController.navigateTo(stage, "/FXML/payment.fxml", controller -> {
            PaymentController c = (PaymentController) controller; // Marks controller as a PaymentController type
            c.setPaymentService(paymentService);
        });
    }
}

    // TODO:
    // Listen and handle calls from the actions buttons

