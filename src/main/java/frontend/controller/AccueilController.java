package frontend.controller;

import frontend.MosquittoApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class AccueilController {
    private MosquittoApp mosquittoApp;

    @FXML
    private void onEntrerClique(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Catalogue.fxml"));
        Parent root = loader.load();
        CatalogueController controller = loader.getController();
        controller.setMosquittoApp(mosquittoApp);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void onVerifierCliquer(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Verifier.fxml"));
        Parent root = loader.load();
        VerifierController controller = loader.getController();
        controller.setMosquittoApp(mosquittoApp);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    public void setMosquittoApp(MosquittoApp mosquittoApp) {
        this.mosquittoApp = mosquittoApp;
    }
}