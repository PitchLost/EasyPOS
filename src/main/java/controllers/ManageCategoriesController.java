package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManageCategoriesController implements Initializable {

    @FXML private ListView<String> categoryList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // TODO: Replace with cached menu keys when storage is set up
        List<String> categories = List.of("Burgers", "Drinks", "Sides");
        categoryList.getItems().addAll(categories);
    }
    @FXML
    public void handleDone() {
        Stage stage = (Stage) categoryList.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleRename() {
        String selected = categoryList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Rename Category");
        dialog.setHeaderText("Rename \"" + selected + "\"");
        dialog.setContentText("New name:");
        dialog.initOwner(categoryList.getScene().getWindow());

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.isBlank()) {
                int index = categoryList.getItems().indexOf(selected);
                categoryList.getItems().set(index, newName);
                // TODO: Update the cached map aswell
            }
        });
    }
    @FXML
    public void handleDelete() {
        String selected = categoryList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Category");
        alert.setHeaderText("Delete \"" + selected + "\"?");
        alert.setContentText("This cannot be undone.");
        alert.initOwner(categoryList.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                categoryList.getItems().remove(selected);
                // TODO: Remove from the cached map aswell
            }
        });
    }
}