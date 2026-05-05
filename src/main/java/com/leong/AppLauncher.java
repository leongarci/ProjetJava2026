package com.leong;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    @Override
    public void start(Stage stage) {
        // Pour l'instant, on crée une fenêtre simple avec un texte
        Label label = new Label("Bienvenue dans la Fabrique de Lunettes !");
        Scene scene = new Scene(new StackPane(label), 640, 480);

        stage.setTitle("Client Lunettes Connectées");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}