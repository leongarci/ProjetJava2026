package frontend.controller;

import bernard_flou.Fabricateur;
import common.CommandeListener;
import common.protocol.CommandeSerializer;
import frontend.MosquittoApp;
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
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CatalogueController implements CommandeListener {
    @FXML private Spinner<Integer> spinChatGPT;
    @FXML private Spinner<Integer> spinClaude;
    @FXML private Spinner<Integer> spinBanana;
    @FXML private Spinner<Integer> spinLeChat;
    @FXML private Button btnCommande;
    @FXML private Label labelStatus;

    private Stage stage;
    private MosquittoApp mosquittoApp;
    @FXML
    private void onCommanderClique(ActionEvent event) throws IOException {

        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Map<Fabricateur.TypeLunette, Integer> quantites = new HashMap<>();

        quantites.put(Fabricateur.TypeLunette.CHATGPT, spinChatGPT.getValue());
        quantites.put(Fabricateur.TypeLunette.CLAUDE, spinClaude.getValue());
        quantites.put(Fabricateur.TypeLunette.LE_CHAT, spinLeChat.getValue());
        quantites.put(Fabricateur.TypeLunette.BANANA, spinBanana.getValue());

        System.out.println("Commande passée : " + quantites);

        btnCommande.setDisable(true);
        labelStatus.setText("Commande en cours...");

        String uuid=UUID.randomUUID().toString();
        mosquittoApp.order(CommandeSerializer.serialize(quantites), uuid);

    }

    public void onValidated(String uuid){
        Platform.runLater(() -> {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Afficher.fxml"));
                Parent root = loader.load();
                AfficherController controller = loader.getController();
                controller.setMosquittoApp(mosquittoApp);
                stage.setScene(new Scene(root));
                stage.show();
            }
            catch(IOException e){
                e.printStackTrace();
            }

        });
    };

    public void onCancelled(String uuid, String reason){
        Platform.runLater(() -> {
            labelStatus.setText("Commande annulée : " + reason);
            labelStatus.setStyle("-fx-text-fill: red;");
            btnCommande.setDisable(false);
        });
    };
    public void onError(String uuid, String error){};

    public void setMosquittoApp(MosquittoApp mosquittoApp) {
        mosquittoApp.setListener(this);
        this.mosquittoApp = mosquittoApp;
    }
}