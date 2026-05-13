package FrontEnd.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;

public class VerifierController {

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


        System.out.println("Vérification demandée pour : " + code);
        lblResultat.setText("Analyse du code " + code + "...");
    }
}