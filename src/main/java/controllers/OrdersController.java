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
import java.util.ResourceBundle;

public class OrdersController implements Initializable {
    HomeService homeService = HomeService.getInstance();
    Order selectedOrder = null;

    @FXML private FlowPane orderContainer;
    @FXML private ScrollPane orderScrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderOrders();
    }

    private void renderOrders() {
        orderContainer.getChildren().clear();
        selectedOrder = null;

        for (Order order : homeService.getOrders()) {
            VBox card = new VBox(6);
            card.setPrefWidth(200);
            card.setPrefHeight(120);
            card.setUserData(order);
            setCardStyle(card, false);

            Label name  = new Label(order.getOrderName());
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

        // Reset all cards then highlight selected
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
        homeService.selectOrder(selectedOrder);
       PaymentService paymentService = new PaymentService(homeService.getTotal(), homeService.getActiveOrder());
        Stage stage = (Stage) orderContainer.getScene().getWindow(); // Get the active stage object from a loaded FXML element

        NavigationController.navigateTo(stage, "/FXML/payment.fxml", controller -> {
            PaymentController c = (PaymentController) controller;
            c.setPaymentService(paymentService);
        });
    }

    @FXML
    public void handleSelectOrder() {
        if (selectedOrder == null) return;
        homeService.selectOrder(selectedOrder);
        Stage stage = (Stage) orderContainer.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/home.fxml");
    }

    @FXML
    public void handleToHome() {
        Stage stage = (Stage) orderScrollPane.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/home.fxml", null);
    }
}