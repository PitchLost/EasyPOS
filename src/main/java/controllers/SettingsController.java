package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Environment;
import services.SettingsService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Controller for the settings screen. Leaves bulk of the logic to SettingsService */
// TODO: Add a link for SettingsService
public class SettingsController implements Initializable {
    SettingsService settingsService = new SettingsService();

    @FXML private Button settingsToHome;
    @FXML private Label serverStatus;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateServerStatus();
    }

    private void updateServerStatus() {
        Environment env = settingsService.getEnv();
        if (env.isHost()) {
            try {
                serverStatus.setText("Server: Running | Device IP: " + java.net.InetAddress.getLocalHost().getHostAddress() + "| Port: " + env.getServerPort());
            } catch (java.net.UnknownHostException e) {
                serverStatus.setText("Server: Running | Device IP: Unknown | Port: " + env.getServerPort());
            }
        } else if (env.isOrdersMode()) {
            serverStatus.setText("Server: Client Mode | Connected to: " + env.getServerAddress() + ":" + env.getServerPort());
        } else {
            serverStatus.setText("Server: Not Running");
        }
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
            updateServerStatus();
        });
    }

    @FXML
    public void startOrdersMode() {
        // Step 1: Get the host IP
        TextInputDialog ipDialog = new TextInputDialog("192.168.1");
        ipDialog.setTitle("Connect to Host");
        ipDialog.setHeaderText("Host IP Address");
        ipDialog.setContentText("Enter the host device's IP address:");
        ipDialog.initOwner(settingsToHome.getScene().getWindow());

        ipDialog.showAndWait().ifPresent(ip -> {
            if (ip.isBlank()) return;

            // Step 2: Get the port
            TextInputDialog portDialog = new TextInputDialog("1000");
            portDialog.setTitle("Connect to Host");
            portDialog.setHeaderText("Host Port");
            portDialog.setContentText("Enter the host port:");
            portDialog.initOwner(settingsToHome.getScene().getWindow());

            portDialog.showAndWait().ifPresent(port -> {
                if (port.isBlank()) return;

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Enter Orders Mode");
                alert.setHeaderText("Connect to " + ip + ":" + port + "?");
                alert.setContentText("This will enter orders mode. You will not be able to create or manage orders from this device.");
                alert.initOwner(settingsToHome.getScene().getWindow());

                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        // Save the IP and port to environment before switching
                        settingsService.saveServerAddress(ip, Integer.parseInt(port));
                        Stage stage = (Stage) settingsToHome.getScene().getWindow();
                        NavigationController.navigateTo(stage, "/FXML/orders2.fxml");
                        settingsService.enterOrdersMode(Integer.parseInt(port));
                    }
                });
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