package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Order;
import services.HomeService;
import services.PaymentService;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/** The controller for the orders view screen. Also has an option to display old orders too. This one doesnt follow the normal function layout
 * because pretty much every FXML handler has a private funciton that it also uses so it's more maintainable in this format.*/
public class OrdersController implements Initializable {
    HomeService homeService = HomeService.getInstance();
    Order selectedOrder = null;
    boolean viewingOldOrders = false;
    private static final int MAX_DISPLAY_OLD_ORDERS = 100; // TODO: Make user configurable

    // FXML Elements
    @FXML private FlowPane orderContainer;
    @FXML private ScrollPane orderScrollPane;
    @FXML private Button toggleViewButton;

    // TODO: Delete this from FXML cause they are unused and crash if undeclared here
    @FXML private Button checkoutButton;
    @FXML private Button selectButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderOrders();
        updateToggleButton();
    }

    // FXML HANDLERS

    @FXML
    public void handleToggleOldOrders() {
        viewingOldOrders = !viewingOldOrders;
        updateToggleButton();
        renderOrders();
    }



    private void renderOrders() {
        orderContainer.getChildren().clear();
        selectedOrder = null;

        ArrayList<Order> oldOrders = homeService.getOldOrders();
        List<Order> ordersToShow = viewingOldOrders
                ? oldOrders.subList(Math.max(0, oldOrders.size() - MAX_DISPLAY_OLD_ORDERS), oldOrders.size())
                : homeService.getOrders();

        for (Order order : ordersToShow) {
            VBox card = new VBox(6);
            card.setPrefWidth(200);
            card.setPrefHeight(120);
            card.setUserData(order);
            setCardStyle(card, false);

            Label name = new Label(order.getOrderName());
            name.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14; -fx-font-weight: bold;");

            Label index = new Label("Order " + order.getOrderIndex());
            index.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12;");

            Label items = new Label(order.getOrderItems().size() + " items");
            items.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12;");

            Label total = new Label("$" + order.getOrderTotal().toPlainString());
            total.setStyle("-fx-text-fill: #0ced48; -fx-font-size: 13; -fx-font-weight: bold;");

            card.getChildren().addAll(name, index, items, total);
            card.setOnMouseClicked(e -> handleOrderCardClicked(order, card));
            orderContainer.getChildren().add(card);
        }
    }

    private void handleOrderCardClicked(Order order, VBox card) {
        selectedOrder = order;

        for (var node : orderContainer.getChildren()) {
            setCardStyle((VBox) node, false);
        }
        setCardStyle(card, true);
    }

    private void setCardStyle(VBox card, boolean selected) {
        card.setStyle(
                "-fx-background-color: " + (selected ? "#1e3a5f" : "#152645") + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: " + (selected ? "#e8a020" : "#333333") + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: " + (selected ? "2" : "1") + ";" +
                        "-fx-padding: 12;"
        );
    }

    @FXML
    public void handleNewOrder() {
        homeService.newOrder();
        renderOrders();
    }

    @FXML
    public void handleCheckoutOrder() {
        if (selectedOrder == null) return;

        Order orderToUse = selectedOrder;
        if (viewingOldOrders) {
            orderToUse = homeService.recoverOrder(selectedOrder);
        }

        homeService.selectOrder(orderToUse);
        PaymentService paymentService = new PaymentService(homeService.getTotal(), homeService.getActiveOrder());
        Stage stage = (Stage) orderContainer.getScene().getWindow();

        NavigationController.navigateTo(stage, "/FXML/payment.fxml", controller -> {
            PaymentController c = (PaymentController) controller;
            c.setPaymentService(paymentService);
        });
    }

    @FXML
    public void handleSelectOrder() {
        if (selectedOrder == null) return;

        if (viewingOldOrders) {
            homeService.recoverOrder(selectedOrder);
        } else {
            homeService.selectOrder(selectedOrder);
        }

        Stage stage = (Stage) orderContainer.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/home.fxml");
    }

    @FXML
    public void handleToHome() {
        Stage stage = (Stage) orderScrollPane.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/home.fxml", null);
    }

    // HELPER FUNCTIONS
    private void updateToggleButton() {
        toggleViewButton.setText(viewingOldOrders ? "View Active Orders" : "View Old Orders");
        toggleViewButton.setStyle(viewingOldOrders
                ? "-fx-background-color: #46b019; -fx-text-fill: #ffffff; -fx-font-size: 14; -fx-padding: 12; -fx-background-radius: 6;"
                : "-fx-background-color: #ff0000; -fx-text-fill: #ffffff; -fx-font-size: 14; -fx-padding: 12; -fx-background-radius: 6;"
        );
    }
}