package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import services.CacheService;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * The controller for the category management modal. Very similar to {@link ManageItemsController} but both control a different modal.
 */

public class ManageCategoriesController implements Initializable {
    CacheService caching = new CacheService();
    @FXML private ListView<String> categoryList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ArrayList<String> categories = caching.loadCategories();
        categoryList.getItems().addAll(categories);
    }
    // FXML HANDLERS
    @FXML
    public void handleDone() {
        Stage stage = (Stage) categoryList.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleCreate() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Category");
        dialog.setHeaderText("New Category");
        dialog.setContentText("Category name:");
        dialog.initOwner(categoryList.getScene().getWindow());

        dialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;
            categoryList.getItems().add(name);
            caching.addCategory(name);
        });
    }

    @FXML
    public void handleRename() {
        String selected = categoryList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String originalName = categoryList.getSelectionModel().getSelectedItem();
        TextInputDialog dialog = new TextInputDialog(selected);
        dialog.setTitle("Rename Category");
        dialog.setHeaderText("Rename \"" + selected + "\"");
        dialog.setContentText("New name:");
        dialog.initOwner(categoryList.getScene().getWindow());

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.isBlank()) {
                int index = categoryList.getItems().indexOf(selected);
                categoryList.getItems().set(index, newName);
                caching.updateCategory(originalName, newName);
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
                caching.deleteCategory(selected);
            }
        });
    }
}