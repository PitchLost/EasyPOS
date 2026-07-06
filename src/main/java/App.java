import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Environment;
import server.NetworkManager;
import services.CacheService;
import services.HomeService;

/**
 * The entrypoint for the program. This sets up FXML, server if needed and navigates to the home screen, and initialises the {@link HomeService}
 */
public class App extends Application {
    CacheService caching = new CacheService();
    NetworkManager networkManager = NetworkManager.getInstance();

    @Override
    public void start(Stage stage) throws Exception {
        HomeService.getInstance().init(); // Do this here bc it breaks everything if it's called later in runtime

        // Start the server if in host mode or in orders mode
        Environment env = caching.loadEnv();
        if (env.isHost()) {
            networkManager.startHostMode(env.getServerPort());
        } else if (env.isOrdersMode()) {
            networkManager.switchToClientMode(env.getServerPort());
        }


        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/home.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("EasyPOS");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}