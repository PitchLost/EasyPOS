

package controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/** A controller to help with navigation around different FXML screens. I used Claude.ai to generate this because I didn't even know where to start but in the classic AI way
 * it generated a very bad chunk of code. So this is it molded down into something that works by me (But is still AI generated code)*/
public class NavigationController {
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
            // Swap the root if a scene already exists
            if (stage.getScene() != null) {
                stage.getScene().setRoot(root);
            } else {
                stage.setScene(new Scene(root));
                stage.setMaximized(true);
            }

            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }
}