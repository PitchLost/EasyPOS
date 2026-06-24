/**
 * This is primarily claude generated barring a few changes made by me. I am not very familliar with managing this many screens in javafx so had to pull something
 * out to make it work. It seems to work pretty ok and is pretty simple but will be in a much different format to the rest of the code
 */

// TODO: Remake this myself!

package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NavigationController {


    private static final Map<String, Object> dataStore = new HashMap<>();

    /** Store a value by key so any controller can retrieve it later. */
    public static void put(String key, Object value) {
        dataStore.put(key, value);
    }

    /** Retrieve a stored value by key. Returns null if not found. */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        return (T) dataStore.get(key);
    }

    /** Remove a value from the store. */
    public static void remove(String key) {
        dataStore.remove(key);
    }

    /** Clear all stored data. */
    public static void clearAll() {
        dataStore.clear();
    }

    @FunctionalInterface
    public interface ControllerInitialiser {
        void initialise(Object controller);
    }


    public static void navigateTo(Stage stage, String fxmlPath) {
        navigateTo(stage, fxmlPath, null);
    }


    public static void navigateTo(Stage stage, String fxmlPath, ControllerInitialiser initialiser) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationController.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (initialiser != null) {
                initialiser.initialise(loader.getController());
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    public static Stage openWindow(String fxmlPath, String title) {
        return openWindow(fxmlPath, title, null);
    }


    public static Stage openWindow(String fxmlPath, String title, ControllerInitialiser initialiser) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationController.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (initialiser != null) {
                initialiser.initialise(loader.getController());
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            return stage;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    public static void openModal(Stage owner, String fxmlPath, String title) {
        openModal(owner, fxmlPath, title, null);
    }

    public static void openModal(Stage owner, String fxmlPath, String title, ControllerInitialiser initialiser) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationController.class.getResource(fxmlPath));
            Parent root = loader.load();

            if (initialiser != null) {
                initialiser.initialise(loader.getController());
            }

            Stage modal = new Stage();
            modal.setTitle(title);
            modal.setScene(new Scene(root));
            modal.initModality(Modality.WINDOW_MODAL);
            modal.initOwner(owner);
            modal.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    public static void close(Stage stage) {
        stage.close();
    }
}