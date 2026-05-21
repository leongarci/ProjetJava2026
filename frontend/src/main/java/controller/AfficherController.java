package controller;

import java.util.List;

import app.MosquittoApp;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import protocol.CommandeListener;
import protocol.LivraisonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AfficherController implements CommandeListener {

    private MosquittoApp mosquittoApp;
    private static final Logger logger = LoggerFactory.getLogger(AfficherController.class);

    @FXML private Button btnRetour;
    @FXML private ListView<String> listNumerosSerie;
    @FXML private Label lblStatut;
    @FXML private Label statusDot;
    @FXML private ProgressIndicator progressIndicator;

    @FXML
    public void initialize() {
        lblStatut.setText("En attente de fabrication par l'usine...");
        // Spinner visible au départ
        progressIndicator.setVisible(true);
        // Placeholder personnalisé
        listNumerosSerie.setPlaceholder(
                new Label("Les numéros de série apparaîtront ici."));
    }

    public void setMosquittoApp(MosquittoApp mosquittoApp) {
        this.mosquittoApp = mosquittoApp;
        mosquittoApp.setListener(this);
    }

    @Override
    public void onDelivery(String uuid, String serials) {
        List<String> lunettes = LivraisonSerializer.deserialize(serials);
        Platform.runLater(() -> {
            // Cacher le spinner
            progressIndicator.setVisible(false);
            // Mettre à jour le dot et le texte
            statusDot.getStyleClass().setAll("status-dot-ok");
            lblStatut.setText("Fabrication terminée ! "
                    + lunettes.size() + " paire(s) livrée(s).");
            listNumerosSerie.getItems().addAll(lunettes);
            btnRetour.setDisable(false);
        });
    }

    @FXML
    private void onRetourClique(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/Accueil.fxml"));
            Parent root = loader.load();
            AccueilController controller = loader.getController();
            controller.setMosquittoApp(mosquittoApp);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Failed to return to Accueil view", e);
        }
    }

    @Override
    public void onError(String uuid, String error) {
        Platform.runLater(() -> {
            progressIndicator.setVisible(false);
            statusDot.getStyleClass().setAll("status-dot-error");
            lblStatut.setText("Erreur : " + error);
            btnRetour.setDisable(false);
        });
    }
}