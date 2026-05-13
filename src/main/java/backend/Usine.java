package backend;
import bernard_flou.Fabricateur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

public class Usine {

    private final Fabricateur fabricateur;
    private static final Logger logger = LoggerFactory.getLogger(Usine.class);
    private final Map<String, Fabricateur.TypeLunette> produites = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public Usine(Fabricateur fabricateur) {
        this.fabricateur = fabricateur;
    }

    public List<Fabricateur.Lunette> produire(Map<Fabricateur.TypeLunette, Integer> typesLunettes) {
        List<CompletableFuture<List<Fabricateur.Lunette>>> futures = typesLunettes.entrySet().stream()
                .map(entry -> CompletableFuture.supplyAsync(
                        () -> produireType(entry.getKey(), entry.getValue()),
                        executorService
                ))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void valider(Map<Fabricateur.TypeLunette, Integer> typesLunettes) {
        if (typesLunettes.isEmpty()) {
            throw new IllegalArgumentException("Il n'y a pas de lunettes à fabriquer");
        }
        int quantite = typesLunettes.values().stream().reduce(0, Integer::sum);
        if (quantite <= 0) {
            throw new IllegalArgumentException("Le nombre de lunettes à fabriquer est inférieur ou égal à 0");
        }
        for (var entry : typesLunettes.entrySet()) {
            int q = entry.getValue();
            if (q < 0 || q > 9)
                throw new IllegalArgumentException("La quantité de " + entry.getKey() + " est invalide : " + q);
        }
    }

    private List<Fabricateur.Lunette> produireType(Fabricateur.TypeLunette type, int quantiteTotale) {
        logger.info("Fabrication de {} {}", quantiteTotale, type);
        List<Fabricateur.Lunette> resultat = new ArrayList<>();
        int capacite = fabricateur.getCapacity();
        int quantiteRestante = quantiteTotale;

        while (quantiteRestante > 0) {
            int quantiteCycle = min(quantiteRestante, capacite);
            Fabricateur.TypeLunette[] emplacements = new Fabricateur.TypeLunette[quantiteCycle];
            Arrays.fill(emplacements, type);

            synchronized (fabricateur) {
                fabricateur.configurer(emplacements);
                for (int i = 0; i < quantiteCycle; i++) {
                    Fabricateur.Lunette lunette = fabricateur.fabriquer(type);
                    resultat.add(lunette);
                    produites.put(lunette.serial, lunette.type);
                }
                quantiteRestante -= quantiteCycle;
            }
        }

        return resultat;
    }

    public CompletableFuture<List<Fabricateur.Lunette>> produireAsync(Map<Fabricateur.TypeLunette, Integer> typesLunettes) {
        return CompletableFuture.supplyAsync(
                () -> produire(typesLunettes),
                executorService
        );
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, Fabricateur.TypeLunette> getSerialsProduits() {
        return produites;
    }
}
