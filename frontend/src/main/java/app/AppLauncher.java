package app;

import controller.AccueilController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        MosquittoApp mosquittoApp = new MosquittoApp("app-client");

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Accueil.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        AccueilController controller = fxmlLoader.getController();
        controller.setMosquittoApp(mosquittoApp);
        stage.setTitle("La Fabrique de Lunettes");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> mosquittoApp.disconnect());
    }

    public static void main(String[] args) {
        launch();
    }
}
