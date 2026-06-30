package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import models.Order;
import services.HomeService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/** Base controller for any screen that displays orders as a grid of cards.
 * Subclasses customize which orders are shown and what happens when a card is selected. */
public abstract class BaseOrderController implements Initializable {
    protected HomeService homeService = HomeService.getInstance();
    protected Order selectedOrder = null;

    @FXML protected FlowPane orderContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderOrders();
    }

    /** Subclasses provide the list of orders to display. */
    protected abstract List<Order> getOrdersToDisplay();

    protected void renderOrders() {
        orderContainer.getChildren().clear();
        selectedOrder = null;

        for (Order order : getOrdersToDisplay()) {
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

            Label time = new Label(order.getFormattedTimestamp());
            time.setStyle("-fx-text-fill: #0ced48; -fx-font-size: 9;");

            card.getChildren().addAll(name, index, items, total, time);
            card.setOnMouseClicked(e -> handleOrderCardClicked(order, card));
            orderContainer.getChildren().add(card);
        }
    }

    protected void handleOrderCardClicked(Order order, VBox card) {
        selectedOrder = order;
        for (var node : orderContainer.getChildren()) {
            setCardStyle((VBox) node, false);
        }
        setCardStyle(card, true);
    }

    protected void setCardStyle(VBox card, boolean selected) {
        card.setStyle(
                "-fx-background-color: " + (selected ? "#1e3a5f" : "#152645") + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-color: " + (selected ? "#e8a020" : "#333333") + ";" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-width: " + (selected ? "2" : "1") + ";" +
                        "-fx-padding: 12;"
        );
    }
}