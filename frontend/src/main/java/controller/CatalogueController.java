package controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import app.MosquittoApp;
import bernard_flou.Fabricateur;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import protocol.CommandeListener;
import protocol.CommandeSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatalogueController implements CommandeListener {

    // Spinners
    @FXML private Spinner<Integer> spinChatGPT;
    @FXML private Spinner<Integer> spinClaude;
    @FXML private Spinner<Integer> spinBanana;
    @FXML private Spinner<Integer> spinLeChat;

    // Images
    @FXML private ImageView imgChatGPT;
    @FXML private ImageView imgClaude;
    @FXML private ImageView imgLeChat;
    @FXML private ImageView imgBanana;

    // Labels noms
    @FXML private Label nameChatGPT;
    @FXML private Label nameClaude;
    @FXML private Label nameLeChat;
    @FXML private Label nameBanana;

    // Labels descriptions
    @FXML private Label descChatGPT;
    @FXML private Label descClaude;
    @FXML private Label descLeChat;
    @FXML private Label descBanana;

    // Labels prix
    @FXML private Label priceChatGPT;
    @FXML private Label priceClaude;
    @FXML private Label priceLeChat;
    @FXML private Label priceBanana;

    // Badge
    @FXML private Label badgeBanana;

    @FXML private Button btnCommande;
    @FXML private Label labelStatus;

    private Stage stage;
    private MosquittoApp mosquittoApp;
    private static final Logger logger = LoggerFactory.getLogger(CatalogueController.class);

    // Classe interne POJO pour le JSON
    private static class Product {
        String id;
        String name;
        double price;
        String badge;
        String description;
    }

    @FXML
    public void initialize() {
        // Charger le JSON
        try (var stream = getClass().getResourceAsStream("/assets/products.json")) {
            if (stream == null) {
                logger.warn("products.json introuvable");
                return;
            }
            Type listType = new TypeToken<List<Product>>(){}.getType();
            List<Product> products = new Gson().fromJson(
                    new InputStreamReader(stream), listType);

            for (Product p : products) {
                // Charger l'image via classpath
                URL imgUrl = getClass().getResource("/assets/" + p.id + ".png");
                Image image = imgUrl != null
                        ? new Image(imgUrl.toExternalForm())
                        : null;

                switch (p.id) {
                    case "chatgpt" -> {
                        if (image != null) imgChatGPT.setImage(image);
                        nameChatGPT.setText(p.name);
                        descChatGPT.setText(p.description);
                        priceChatGPT.setText(String.format("%.2f EUR", p.price));
                    }
                    case "claude" -> {
                        if (image != null) imgClaude.setImage(image);
                        nameClaude.setText(p.name);
                        descClaude.setText(p.description);
                        priceClaude.setText(String.format("%.2f EUR", p.price));
                    }
                    case "le_chat" -> {
                        if (image != null) imgLeChat.setImage(image);
                        nameLeChat.setText(p.name);
                        descLeChat.setText(p.description);
                        priceLeChat.setText(String.format("%.2f EUR", p.price));
                    }
                    case "banana" -> {
                        if (image != null) imgBanana.setImage(image);
                        nameBanana.setText(p.name);
                        descBanana.setText(p.description);
                        priceBanana.setText(String.format("%.2f EUR", p.price));
                        if (p.badge != null && !p.badge.isEmpty()) {
                            badgeBanana.setText(p.badge);
                        } else {
                            badgeBanana.setVisible(false);
                            badgeBanana.setManaged(false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erreur chargement products.json", e);
        }
    }

    @FXML
    private void onCommanderClique(ActionEvent event) throws IOException {
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Map<Fabricateur.TypeLunette, Integer> quantites = new HashMap<>();
        quantites.put(Fabricateur.TypeLunette.CHATGPT, spinChatGPT.getValue());
        quantites.put(Fabricateur.TypeLunette.CLAUDE, spinClaude.getValue());
        quantites.put(Fabricateur.TypeLunette.LE_CHAT, spinLeChat.getValue());
        quantites.put(Fabricateur.TypeLunette.BANANA, spinBanana.getValue());

        int total = quantites.values().stream().mapToInt(Integer::intValue).sum();
        if (total == 0) {
            labelStatus.setText("Selectionnez au moins une paire.");
            return;
        }

        logger.info("Commande passee : {}", quantites);
        btnCommande.setDisable(true);
        labelStatus.setText("Commande en cours...");

        String uuid = UUID.randomUUID().toString();
        mosquittoApp.order(CommandeSerializer.serialize(quantites), uuid);
    }

    @FXML
    private void onAnnulerClique(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/Accueil.fxml"));
            Parent root = loader.load();
            AccueilController controller = loader.getController();
            controller.setMosquittoApp(mosquittoApp);
            Stage s = (Stage) ((Node) event.getSource()).getScene().getWindow();
            s.setScene(new Scene(root));
        } catch (IOException e) {
            logger.error("Echec retour accueil", e);
        }
    }

    public void onValidated(String uuid) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/views/Afficher.fxml"));
                Parent root = loader.load();
                AfficherController controller = loader.getController();
                controller.setMosquittoApp(mosquittoApp);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                logger.error("Failed to load Afficher view", e);
            }
        });
    }

    public void onCancelled(String uuid, String reason) {
        Platform.runLater(() -> {
            if (labelStatus != null) {
                labelStatus.setText("Commande annulee : " + reason);
                labelStatus.setStyle("-fx-text-fill: #9b1c1c;");
                btnCommande.setDisable(false);
            }
        });
    }

    @Override
    public void onError(String uuid, String error) {
        Platform.runLater(() -> {
            labelStatus.setText("Erreur : " + error);
            labelStatus.setStyle("-fx-text-fill: #9b1c1c;");
            btnCommande.setDisable(false);
        });
    }

    public void setMosquittoApp(MosquittoApp mosquittoApp) {
        mosquittoApp.setListener(this);
        this.mosquittoApp = mosquittoApp;
    }
}