package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.SettingsService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Controller for the settings screen. Leaves bulk of the logic to SettingsService */
// TODO: Add a link for SettingsService
public class SettingsController implements Initializable {
    SettingsService settingsService = new SettingsService();

    @FXML private Button settingsToHome;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    public void toHome() {
        Stage stage = (Stage) settingsToHome.getScene().getWindow();
        NavigationController.navigateTo(stage, "/FXML/home.fxml", null);
    }

    @FXML
    private void openManageCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/modals/manageCategories.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(settingsToHome.getScene().getWindow());
            modal.setTitle("Manage Categories");
            modal.setScene(new Scene(root, 600, 400));
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openManageItems() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/modals/manageItems.fxml"));
            Parent root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initOwner(settingsToHome.getScene().getWindow());
            modal.setTitle("Manage Items");
            modal.setScene(new Scene(root, 600, 400));
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Server stuff
    @FXML
    public void startHostMode() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Start Host Mode");
        dialog.setHeaderText("Server Port");
        dialog.setContentText("Port to run on:");
        // Set to 8000 by default
        dialog.initOwner(settingsToHome.getScene().getWindow());

        dialog.showAndWait().ifPresent(name -> {
            settingsService.enterHostMode(Integer.parseInt(name));
        });
    }

    @FXML
    public void startOrdersMode() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Connect to different device");
        dialog.setHeaderText("Server Port");
        dialog.setContentText("Port to run on:");
        // Set to 8000 by default
        dialog.initOwner(settingsToHome.getScene().getWindow());

        dialog.showAndWait().ifPresent(name -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Enter Orders Mode");
            alert.setHeaderText("Switch to orders mode?");
            alert.setContentText("This will enter the orders mode. You will not be able to create/manage orders from this mode");
            alert.initOwner(settingsToHome.getScene().getWindow());

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Stage stage = (Stage) settingsToHome.getScene().getWindow();
                    NavigationController.navigateTo(stage, "/FXML/orders2.fxml");
                    settingsService.enterOrdersMode(Integer.parseInt(name));
                }
            });
        });
    }

    @FXML
    public void resetServer() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Server");
        alert.setHeaderText("Reset Server");
        alert.setContentText("This will disconnect from any other devices and shut down the hosted server if active");
        alert.initOwner(settingsToHome.getScene().getWindow());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                settingsService.resetServerSettings();
            }
        });
    }
}