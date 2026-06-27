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

import java.net.URL;
import java.util.ResourceBundle;

public class OrdersController implements Initializable {
    HomeService homeService = HomeService.getInstance();

    @FXML private FlowPane orderContainer;
    @FXML private ScrollPane orderScrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderOrders();
    }

    private void renderOrders() {
        orderContainer.getChildren().clear();

        for (Order order : homeService.getOrders()) {
            VBox card = new VBox(6);
            card.setPrefWidth(200);
            card.setPrefHeight(120);
            card.setStyle(
                    "-fx-background-color: #152645;" +
                            "-fx-background-radius: 8;" +
                            "-fx-border-color: #333333;" +
                            "-fx-border-radius: 8;" +
                            "-fx-border-width: 1;" +
                            "-fx-padding: 12;"
            );

            Label name  = new Label(order.getOrderName());
            name.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14; -fx-font-weight: bold;");

            Label index = new Label("Order " + order.getOrderIndex());
            index.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12;");

            Label items = new Label(order.getOrderItems().size() + " items");
            items.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12;");

            Label total = new Label("$" + order.getOrderTotal().toPlainString());
            total.setStyle("-fx-text-fill: #0ced48; -fx-font-size: 13; -fx-font-weight: bold;");

            card.getChildren().addAll(name, index, items, total);
            card.setOnMouseClicked(e -> handleOrderSelected(order));
            orderContainer.getChildren().add(card);
        }
    }

    private void handleOrderSelected(Order order) {
        homeService.selectOrder(order);
        Stage stage = (Stage) orderScrollPane.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/home.fxml", null);
    }

    @FXML
    public void handleNewOrder() {
        homeService.newOrder();
        renderOrders();
    }

    @FXML
    public void handleToHome() {
        Stage stage = (Stage) orderScrollPane.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/home.fxml", null);
    }
}