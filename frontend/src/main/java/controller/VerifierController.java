package controller;

import app.MosquittoApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import protocol.CommandeListener;

public class VerifierController implements CommandeListener {

    private MosquittoApp mosquittoApp;

    @FXML
    private TextField txtNumeroAVerifier;

    @FXML
    private Label lblResultat;

    @FXML
    private void onBoutonVerifierClick() {
        String code = txtNumeroAVerifier.getText();

        if (code == null || code.isEmpty()) {
            lblResultat.setText("Veuillez entrer un numéro.");
            return;
        }
        mosquittoApp.askSerials(code);

        lblResultat.setText("Analyse du code " + code + "...");
    }

    public void setMosquittoApp(MosquittoApp mosquittoApp) {
        mosquittoApp.setListener(this);
        this.mosquittoApp = mosquittoApp;
    }

    public void onChecked(String uuid, String typeLunette) {
        Platform.runLater(() -> {

            lblResultat.setText(typeLunette);
        });
    }

    @FXML
    private void onRetourClique(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Accueil.fxml"));
            Parent root = loader.load();
            AccueilController controller = loader.getController();
            controller.setMosquittoApp(mosquittoApp);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
