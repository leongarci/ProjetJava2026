package factory;

import static java.lang.Math.min;

import java.util.*;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bernard_flou.Fabricateur;

public class Usine {

    private final Fabricateur fabricateur;
    private static final Logger logger = LoggerFactory.getLogger(Usine.class);
    private final Map<String, Fabricateur.TypeLunette> produites = new ConcurrentHashMap<>();

    private final ExecutorService workerService = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r);
        t.setDaemon(false);
        return t;
    });
    private final LinkedBlockingQueue<CommandeEnAttente> commandesQueue = new LinkedBlockingQueue<>();

    public Usine(Fabricateur fabricateur) {
        this.fabricateur = fabricateur;
        workerService.submit(() -> {
            List<CommandeEnAttente> commandes = new ArrayList<>();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    CommandeEnAttente premiere = commandesQueue.take();
                    commandes.add(premiere);
                    commandesQueue.drainTo(commandes);
                    traiterCommandesMutualisees(commandes);
                    commandes.clear();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.info("Mutualisation interrompue");
                    break;
                }
            }
        });
    }

    public List<Fabricateur.Lunette> produire(Map<Fabricateur.TypeLunette, Integer> typesLunettes) {
        List<Fabricateur.Lunette> resultat = new ArrayList<>();
        for (var entry : typesLunettes.entrySet()) {
            resultat.addAll(produireType(entry.getKey(), entry.getValue()));
        }
        return resultat;
    }

    public CompletableFuture<List<Fabricateur.Lunette>> ajouterCommandeMutualisee(
            String uuid,
            Map<Fabricateur.TypeLunette, Integer> commande) {
        CompletableFuture<List<Fabricateur.Lunette>> future = new CompletableFuture<>();
        CommandeEnAttente cmd = new CommandeEnAttente(uuid, commande, future);
        try {
            commandesQueue.put(cmd);
            logger.info("Commande {} ajoutée à la queue", uuid);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            future.completeExceptionally(
                    new RuntimeException("Erreur ajout de la commande", e)
            );
        }
        return future;
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
            if (q < 0 || q > 9) {
                throw new IllegalArgumentException("La quantité de " + entry.getKey() + " est invalide : " + q);
            }
        }
    }

    private void traiterCommandesMutualisees(List<CommandeEnAttente> commandes) {
        if (commandes.isEmpty()) return;
        logger.info("Traitement de {} commande(s) mutualisée(s)", commandes.size());
        try {
            Map<Fabricateur.TypeLunette, Integer> commandeMutualisee = new HashMap<>();
            for (CommandeEnAttente cmd : commandes) {
                cmd.commande().forEach((type, qte) ->
                        commandeMutualisee.merge(type, qte, Integer::sum)
                );
            }
            List<Fabricateur.Lunette> lunettesProduites = produire(commandeMutualisee);
            distribuerLunettes(commandes, lunettesProduites);
        } catch (Exception e) {
            logger.error("Erreur lors du traitement des commandes mutualisées", e);
            commandes.forEach(cmd -> cmd.future().completeExceptionally(e));
        }
    }

    private void distribuerLunettes(
            List<CommandeEnAttente> commandes,
            List<Fabricateur.Lunette> lunettesProduites) {

        Map<Fabricateur.TypeLunette, Queue<Fabricateur.Lunette>> lunettesParType = new HashMap<>();
        for (Fabricateur.Lunette lunette : lunettesProduites) {
            lunettesParType.computeIfAbsent(lunette.type, k -> new LinkedList<>()).offer(lunette);
        }
        for (CommandeEnAttente cmd : commandes) {
            List<Fabricateur.Lunette> lunettesClient = new ArrayList<>();
            for (var entry : cmd.commande().entrySet()) {
                Queue<Fabricateur.Lunette> disponibles = lunettesParType.get(entry.getKey());
                if (disponibles != null) {
                    for (int i = 0; i < entry.getValue() && !disponibles.isEmpty(); i++) {
                        lunettesClient.add(disponibles.poll());
                    }
                }
            }
            cmd.future().complete(lunettesClient);
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

    public void shutdown() {
        workerService.shutdown();
        try {
            if (!workerService.awaitTermination(5, TimeUnit.SECONDS)) {
                workerService.shutdownNow();
            }
        } catch (InterruptedException e) {
            workerService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public Map<String, Fabricateur.TypeLunette> getSerialsProduits() {
        return produites;
    }

    record CommandeEnAttente(
            String uuid,
            Map<Fabricateur.TypeLunette, Integer> commande,
            CompletableFuture<List<Fabricateur.Lunette>> future
    ) {}
}
