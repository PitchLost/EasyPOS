package controllers;
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
    HomeService homeService = new HomeService();
    PaymentService paymentService = new PaymentService(new BigDecimal("350.00"), new ArrayList<>()); // TODO: Not init this here??
    ArrayList<String> coinButtons;
    ArrayList<String> noteButtons;
    @FXML
    private Text paymentRemaining;
    @FXML
    private Text paymentPayed;
    @FXML
    private HBox coinPane;
    @FXML
    private HBox notePane;
    BigDecimal totalDue = paymentService.getTotalDue();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        coinButtons = new ArrayList<>(List.of("0.10", "0.20", "0.50", "1.00", "2.00")); // Default values, may or may not be overridden by caching
        noteButtons = new ArrayList<>(List.of("5", "10", "20", "50", "100"));

        paymentPayed.setText("0.00");
        paymentRemaining.setText(totalDue.setScale(2, RoundingMode.HALF_UP).toString());
        renderItems();
    }
    // Render items on the GUI


    private void renderItems() {
        coinPane.getChildren().clear();

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

    private void handleButtonClicked(String incrementValue) {
        BigDecimal currentPayed = new BigDecimal(paymentPayed.getText());
        BigDecimal increment = new BigDecimal(incrementValue);
        BigDecimal newPayed = currentPayed.add(increment);

        paymentPayed.setText(newPayed.setScale(2, RoundingMode.HALF_UP).toString());

        BigDecimal remaining = totalDue.subtract(newPayed);
        paymentRemaining.setText(remaining.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP).toString());
    }

    // Setter for the payment service, this way we dont really have to pass paymentServices around different stages
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void markOrderComplete() {
        System.out.println("Marking order complete");
    }

    public void toHome() {
        System.out.println("To home");
    }

    public void toSelectOrder() {
        System.out.println("To select order");
    }

}
