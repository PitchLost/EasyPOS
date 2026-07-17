package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import services.CacheService;
import services.MoneyButtons;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * The controller for the Coin management modal. Very similar to {@link ManageItemsController} but both control a different modal.
 */

public class ManageCoinsController implements Initializable {
    CacheService caching = new CacheService();
    @FXML private ListView<String> coinsList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MoneyButtons moneyButtons = caching.loadMoneyButtons();
        if (moneyButtons == null) return; // no saved buttons yet
        ArrayList<String> coins = moneyButtons.getCoinButtons();
        if (coins == null) return; // no coins saved yet
        coinsList.getItems().addAll(coins);
    }
    // FXML HANDLERS
    @FXML
    private void handleDone() {
        Stage stage = (Stage) coinsList.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCreate() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Coin");
        dialog.setHeaderText("New Coin");
        dialog.setContentText("Coin name:");
        dialog.initOwner(coinsList.getScene().getWindow());

        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;
            coinsList.getItems().add(name);
            caching.addMoneyButton("COIN", name);
        });
    }


    @FXML
    private void handleDelete() {
        String selected = coinsList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Coin");
        alert.setHeaderText("Delete \"" + selected + "\"?");
        alert.setContentText("This cannot be undone.");
        alert.initOwner(coinsList.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                coinsList.getItems().remove(selected);
                caching.deleteMoneyButton("COIN", selected);
            }
        });
    }
}