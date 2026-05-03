package com.leong;
import bernard_flou.Fabricateur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

public class Usine {

    private final Fabricateur fabricateur;

    public Usine(Fabricateur fabricateur) {
        this.fabricateur = fabricateur;
    }

    public synchronized List<Fabricateur.Lunette> produire(Map<Fabricateur.TypeLunette, Integer> typesLunettes) {
        List<Fabricateur.Lunette> resultat = new ArrayList<>();

        for (var entry : typesLunettes.entrySet()) {
            Fabricateur.TypeLunette type = entry.getKey();
            int quantiteTotale = entry.getValue();
            int capacite = fabricateur.getCapacity();
            int quantiteRestante = quantiteTotale;

            while (quantiteRestante > 0) {
                int quantiteCycle = min(quantiteRestante, capacite);

                // Construction du tableau
                Fabricateur.TypeLunette[] emplacements = new Fabricateur.TypeLunette[quantiteCycle];
                Arrays.fill(emplacements, type);

                // Configuration
                fabricateur.configurer(emplacements);

                // Fabrication
                for (int i = 0; i < quantiteCycle; i++) {
                    resultat.add(fabricateur.fabriquer(type));
                }

                quantiteRestante -= quantiteCycle;
            }
        }

        return resultat;
    }
}