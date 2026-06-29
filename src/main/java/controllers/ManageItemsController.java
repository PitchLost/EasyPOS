package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Item;
import services.CacheService;

import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

/**
 * The controller for the item management modal. Very similar to {@link ManageCategoriesController} but both control a different modal.
 */
public class ManageItemsController implements Initializable {
    CacheService caching = new CacheService();

    @FXML private ListView<Item> itemList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LinkedHashMap<String, ArrayList<Item>> menu = caching.loadMenu();
        if (menu.isEmpty()) return;

        for (String category : menu.keySet()) {
            itemList.getItems().addAll(menu.get(category));
        }

        itemList.getItems().sort(Comparator.comparing(Item::getItemName));
        itemList.getSelectionModel().selectFirst();

        itemList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.getItemName() + " (" + item.getItemCategory() + ") $" + item.getItemPrice());
            }
        });
    }

    @FXML
    public void handleDone() {
        Stage stage = (Stage) itemList.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleNewItem() {
        ArrayList<String> categories = caching.loadCategories();

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("New Item");
        nameDialog.setHeaderText("New Item");
        nameDialog.setContentText("Item name:");
        nameDialog.initOwner(itemList.getScene().getWindow());

        nameDialog.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;

            ChoiceDialog<String> categoryDialog = new ChoiceDialog<>(categories.get(0), categories);
            categoryDialog.setTitle("New Item");
            categoryDialog.setHeaderText("Category for \"" + name + "\"");
            categoryDialog.setContentText("Category:");
            categoryDialog.initOwner(itemList.getScene().getWindow());

            categoryDialog.showAndWait().ifPresent(category -> {
                TextInputDialog priceDialog = new TextInputDialog();
                priceDialog.setTitle("New Item");
                priceDialog.setHeaderText("Price for \"" + name + "\"");
                priceDialog.setContentText("Price:");
                priceDialog.initOwner(itemList.getScene().getWindow());

                priceDialog.showAndWait().ifPresent(price -> {
                    if (price.isBlank()) return;
                    Item newItem = new Item(name, category, new BigDecimal(price), 1, new ArrayList<>());
                    itemList.getItems().add(newItem);
                    itemList.getItems().sort(Comparator.comparing(Item::getItemName));
                    caching.addItem(newItem);
                });
            });
        });
    }

    @FXML
    public void handleRename() {
        Item selected = itemList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getItemName());
        dialog.setTitle("Rename Item");
        dialog.setHeaderText("Rename \"" + selected.getItemName() + "\"");
        dialog.setContentText("New name:");
        dialog.initOwner(itemList.getScene().getWindow());

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.isBlank()) {
                caching.deleteItem(selected); // Delete item from disk while old name still matches
                selected.setName(newName);
                caching.addItem(selected); // Add item to disk now that name has been changed
                itemList.refresh();
            }
        });
    }
    @FXML
    public void handleEditPrice() {
        Item selected = itemList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getItemPrice().toPlainString());
        dialog.setTitle("Edit Price");
        dialog.setHeaderText("Edit price for \"" + selected.getItemName() + "\"");
        dialog.setContentText("New price:");
        dialog.initOwner(itemList.getScene().getWindow());

        dialog.showAndWait().ifPresent(newPrice -> {
            if (!newPrice.isBlank()) {
                selected.setItemPrice(new BigDecimal(newPrice));
                itemList.refresh();
                caching.updateItem(selected);
            }
        });
    }

    @FXML
    public void handleEditCategory() {
        Item selected = itemList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(selected.getItemCategory());
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit category for \"" + selected.getItemName() + "\"");
        dialog.setContentText("New category:");
        dialog.initOwner(itemList.getScene().getWindow());

        dialog.showAndWait().ifPresent(newCategory -> {
            if (!newCategory.isBlank()) {
                selected.setItemCategory(newCategory);
                itemList.refresh();
                caching.updateItem(selected);
            }
        });
    }

    @FXML
    public void handleEditQty() {
        Item selected = itemList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TextInputDialog dialog = new TextInputDialog(String.valueOf(selected.getDefaultQty()));
        dialog.setTitle("Edit Quantity");
        dialog.setHeaderText("Edit default quantity for \"" + selected.getItemName() + "\"");
        dialog.setContentText("New quantity:");
        dialog.initOwner(itemList.getScene().getWindow());

        dialog.showAndWait().ifPresent(newQty -> {
            if (!newQty.isBlank()) {
                selected.setDefaultQty(Integer.parseInt(newQty));
                itemList.refresh();
                caching.updateItem(selected);
            }
        });
    }

    @FXML
    public void handleDelete() {
        Item selected = itemList.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Delete \"" + selected.getItemName() + "\"?");
        alert.setContentText("This cannot be undone.");
        alert.initOwner(itemList.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                itemList.getItems().remove(selected);
                caching.deleteItem(selected);
            }
        });
    }
}