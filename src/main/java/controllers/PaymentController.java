package controllers;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import services.HomeService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import services.PaymentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {
    PaymentService paymentService;
    // An ArrayList for each of the coins and notes on the payment menu. Default values are added in init but also can be configed by user
    ArrayList<String> coinButtons;
    ArrayList<String> noteButtons;

    BigDecimal totalDue;

    // FXML elements
    @FXML
    private Label totalDueLabel;
    @FXML
    private Label paymentRemaining;
    @FXML
    private Label paymentPayed;
    @FXML
    private HBox coinPane;
    @FXML
    private HBox notePane;


    // Init, Sets some default values and placeholder values for FXML elements
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        coinButtons = new ArrayList<>(List.of("0.10", "0.20", "0.50", "1.00", "2.00")); // Default values, may or may not be overridden by caching
        noteButtons = new ArrayList<>(List.of("5", "10", "20", "50", "100"));
        System.out.println("Init complete");
        renderItems();
    }


    // Render items on the GUI
    private void renderItems() {
        coinPane.getChildren().clear();

        // TODO: Merge these into 1 loop?
        for (String coin : coinButtons) {
            Button btn = new Button(coin);
            btn.setPrefWidth(100);
            btn.setPrefHeight(110);
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

            btn.setOnAction(e -> handleButtonClicked(coin));
            coinPane.getChildren().add(btn);
        }

        for (String note : noteButtons) {
            Button btn = new Button(note);
            btn.setPrefWidth(100);
            btn.setPrefHeight(110);
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
            btn.setOnAction(e -> handleButtonClicked(note));
            notePane.getChildren().add(btn);
        }
    }

    // Handlers

    // Handle when a coin or note button gets clicked.
    // It adds the value of the button to the amount payed and removes it from the amount remaining
    private void handleButtonClicked(String incrementValue) {
        BigDecimal currentPayed = new BigDecimal(paymentPayed.getText());
        BigDecimal increment = new BigDecimal(incrementValue);
        BigDecimal newPayed = currentPayed.add(increment);

        paymentPayed.setText(newPayed.setScale(2, RoundingMode.HALF_UP).toString());

        BigDecimal remaining = totalDue.subtract(newPayed);
        paymentRemaining.setText(remaining.setScale(2, RoundingMode.HALF_UP).toString()); // Will this go negative?
    }

    // Helpers:
    private String convertToMoney(BigDecimal amount) {
        return "$"+amount.toPlainString();
    }

    // Setters:


    // Setter for the payment service, this way we dont really have to pass paymentServices around different stages
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
        this.totalDue = paymentService.getTotalDue(); // actually grab it
        totalDueLabel.setText(convertToMoney(totalDue));
        paymentPayed.setText("0.00");
        paymentRemaining.setText(totalDue.setScale(2, RoundingMode.HALF_UP).toString());
    }


    // Action functions
    public void markOrderComplete() {
        System.out.println("Marking order complete");
    }

    public void toHome() {
        System.out.println("To home");
    }

    public void toSelectOrder() {
        System.out.println("To select order");
    }

    //TODO:
    // Make amount remaining not bottom out at 0
}
