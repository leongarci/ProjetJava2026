package com.leong.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;

public class AfficherController {

    @FXML
    private ListView<String> listNumerosSerie;

    @FXML
    private Label lblStatut;

    public void ajouterNumeroSerie(String numero) {
        listNumerosSerie.getItems().add("Lunette livrée : " + numero);
    }

    @FXML
    public void initialize() {
        lblStatut.setText("En attente de fabrication par l'usine...");
    }
}