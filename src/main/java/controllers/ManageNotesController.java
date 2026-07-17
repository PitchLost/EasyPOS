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
 * The controller for the Note management modal. Very similar to {@link ManageItemsController} but both control a different modal.
 */

public class ManageNotesController implements Initializable {
    CacheService caching = new CacheService();
    @FXML private ListView<String> notesList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MoneyButtons moneyButtons = caching.loadMoneyButtons();
        if (moneyButtons == null) return;
        ArrayList<String> notes = moneyButtons.getNoteButtons();
        if (notes == null) return;
        notesList.getItems().addAll(notes);
    }
    // FXML HANDLERS
    @FXML
    private void handleDone() {
        Stage stage = (Stage) notesList.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleCreate() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Note");
        dialog.setHeaderText("New Note");
        dialog.setContentText("Note name:");
        dialog.initOwner(notesList.getScene().getWindow());

        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;
            notesList.getItems().add(name);
            caching.addMoneyButton("NOTE", name);
        });
    }


    @FXML
    private void handleDelete() {
        String selected = notesList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Note");
        alert.setHeaderText("Delete \"" + selected + "\"?");
        alert.setContentText("This cannot be undone.");
        alert.initOwner(notesList.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                notesList.getItems().remove(selected);
                caching.deleteMoneyButton("NOTE", selected);
            }
        });
    }
}