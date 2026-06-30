package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Order;
import models.OrderItem;

import java.util.List;

public class OrderBoardController extends BaseOrderController {
    @FXML private ScrollPane orderScrollPane;

    @Override
    protected List<Order> getOrdersToDisplay() {
        return homeService.getOrders();
    }

    protected void renderOrders() {
        orderContainer.getChildren().clear();
        selectedOrder = null;

        for (Order order : getOrdersToDisplay()) {
            VBox card = new VBox(10);
            card.setPrefWidth(260);
            card.setUserData(order);
            setCardStyle(card, false);

            Label name = new Label(order.getOrderName());
            name.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 24; -fx-font-weight: bold;");

            Label time = new Label(order.getFormattedTimestamp());
            time.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 14;");

            card.getChildren().addAll(name, time);
            addExtraCardContent(card, order); // adds the items list

            card.setOnMouseClicked(e -> handleOrderCardClicked(order, card));
            orderContainer.getChildren().add(card);
        }
    }

    /** Adds a large, easy-to-read items list to each card. */
    protected void addExtraCardContent(VBox card, Order order) {
        List<OrderItem> orderItems = order.getOrderItems();

        for (OrderItem item : orderItems) {
            Label itemLabel = new Label(item.getItemQuantity() + "x  " + item.getItemName());
            itemLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 20; -fx-font-weight: bold;");
            card.getChildren().add(itemLabel);
        }
    }

    @FXML
    public void handleToHome() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave Orders Mode");
        alert.setHeaderText("Switch to standard mode?");
        alert.setContentText("This will leave the orders mode. Come back anytime from the home screen");
        alert.initOwner(orderContainer.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Stage stage = (Stage) orderScrollPane.getScene().getWindow();
                NavigationController.navigateTo(stage, "/FXML/home.fxml", null);
            }
        });
    }
}