package com.leong;
import bernard_flou.Fabricateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

public class Usine {

    private final Fabricateur fabricateur;
    private static final Logger logger = LoggerFactory.getLogger(Usine.class);

    public Usine(Fabricateur fabricateur) {
        this.fabricateur = fabricateur;
    }

    public synchronized List<Fabricateur.Lunette> produire(Map<Fabricateur.TypeLunette, Integer> typesLunettes) {
        if(typesLunettes.isEmpty()) {
            throw new IllegalArgumentException("Il n'y a pas de lunettes à fabriquer");
        }
        int quantite=typesLunettes.values().stream().reduce(0, Integer::sum);
        if(quantite<=0 ) {
            throw new IllegalArgumentException("Le nombre de lunettes à fabriquer est inférieur ou égal à 0");
        }
        List<Fabricateur.Lunette> resultat = new ArrayList<>();

        for (var entry : typesLunettes.entrySet()) {
            logger.info("Fabrication de {} {}", entry.getValue(), entry.getKey());
            Fabricateur.TypeLunette type = entry.getKey();
            int quantiteTotale = entry.getValue();
            if (quantiteTotale<0 || quantiteTotale>9) throw new IllegalArgumentException("La quantité de " + type + " est invalide : " + quantiteTotale);
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