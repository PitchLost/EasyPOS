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

/** The controller for the orders select screen. Also has an option to display old orders too. This one doesnt follow the normal function layout
 * because pretty much every FXML handler has a private funciton that it also uses so it's more maintainable in this format.*/
public class OrdersController extends BaseOrderController {
    boolean viewingOldOrders = false;
    private static final int MAX_DISPLAY_OLD_ORDERS = 100;

    @FXML private Button toggleViewButton;
    @FXML private ScrollPane orderScrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        updateToggleButton();
    }

    @Override
    protected List<Order> getOrdersToDisplay() {
        if (!viewingOldOrders) return homeService.getOrders();
        ArrayList<Order> oldOrders = homeService.getOldOrders();
        return oldOrders.subList(Math.max(0, oldOrders.size() - MAX_DISPLAY_OLD_ORDERS), oldOrders.size());
    }

    @FXML
    public void handleToggleOldOrders() {
        viewingOldOrders = !viewingOldOrders;
        updateToggleButton();
        renderOrders();
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
