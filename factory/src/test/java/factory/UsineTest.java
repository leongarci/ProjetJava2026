package factory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bernard_flou.Fabricateur;

@DisplayName("Tests d'Usine Factory")
class UsineTest {

    private Fabricateur fabricateurMock;
    private Usine usine;
    private static int serialCounter = 0;

    @BeforeEach
    void setUp() {
        fabricateurMock = mock(Fabricateur.class);
        when(fabricateurMock.getCapacity()).thenReturn(5);

        when(fabricateurMock.fabriquer(Fabricateur.TypeLunette.CHATGPT))
                .thenAnswer(inv -> mockLunette(Fabricateur.TypeLunette.CHATGPT, "S-CHATGPT-" + (serialCounter++)));
        when(fabricateurMock.fabriquer(Fabricateur.TypeLunette.BANANA))
                .thenAnswer(inv -> mockLunette(Fabricateur.TypeLunette.BANANA, "S-BANANA-" + (serialCounter++)));
        when(fabricateurMock.fabriquer(Fabricateur.TypeLunette.CLAUDE))
                .thenAnswer(inv -> mockLunette(Fabricateur.TypeLunette.CLAUDE, "S-CLAUDE-" + (serialCounter++)));
        when(fabricateurMock.fabriquer(Fabricateur.TypeLunette.LE_CHAT))
                .thenAnswer(inv -> mockLunette(Fabricateur.TypeLunette.LE_CHAT, "S-LECHAT-" + (serialCounter++)));

        usine = new Usine(fabricateurMock);
    }

    private Fabricateur.Lunette mockLunette(Fabricateur.TypeLunette type, String serial) {
        Fabricateur.Lunette l = spy(new Fabricateur.Lunette(type, serial));
        return l;
    }

    @AfterEach
    void tearDown() {
        usine.shutdown();
        serialCounter = 0;
    }

    @Test
    @DisplayName("Doit lever une exception pour une commande vide")
    void valider_commandeVide_leveException() {
        assertThrows(IllegalArgumentException.class,
                () -> usine.valider(Map.of()));
    }

    @ParameterizedTest(name = "Quantité invalide doit lever exception")
    @ValueSource(ints = {-5, -1, 0, 10, 15})
    @DisplayName("Doit lever une exception pour les quantités invalides")
    void valider_quantitesInvalides_leveException(int quantite) {
        assertThrows(IllegalArgumentException.class,
                () -> usine.valider(Map.of(Fabricateur.TypeLunette.BANANA, quantite)));
    }

    @Test
    @DisplayName("Doit lever une exception pour une quantité supérieure à 9")
    void valider_quantiteSupA9_leveException() {
        assertThrows(IllegalArgumentException.class,
                () -> usine.valider(Map.of(Fabricateur.TypeLunette.CLAUDE, 10)));
    }

    @Test
    @DisplayName("Doit accepter quatre types valides")
    void valider_quatreTypesValides_nepasLeverException() {
        assertDoesNotThrow(() -> usine.valider(Map.of(
                Fabricateur.TypeLunette.CHATGPT, 2,
                Fabricateur.TypeLunette.BANANA, 1,
                Fabricateur.TypeLunette.CLAUDE, 3,
                Fabricateur.TypeLunette.LE_CHAT, 1
        )));
    }

    @Test
    @DisplayName("Doit accepter la quantité maximale valide (9)")
    void valider_quantiteMax_nepasLeverException() {
        assertDoesNotThrow(() -> usine.valider(Map.of(
                Fabricateur.TypeLunette.CHATGPT, 9
        )));
    }

    @Test
    @DisplayName("Doit produire la bonne quantité de CHATGPT")
    void produire_chatgpt_retourneNombreCorrect() {
        List<Fabricateur.Lunette> result = usine.produire(
                Map.of(Fabricateur.TypeLunette.CHATGPT, 3)
        );
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Doit produire la bonne quantité de BANANA")
    void produire_banana_retourneNombreCorrect() {
        List<Fabricateur.Lunette> result = usine.produire(
                Map.of(Fabricateur.TypeLunette.BANANA, 2)
        );
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Doit produire la bonne quantité de LE_CHAT")
    void produire_leChat_retourneNombreCorrect() {
        List<Fabricateur.Lunette> result = usine.produire(
                Map.of(Fabricateur.TypeLunette.LE_CHAT, 1)
        );
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Doit produire la bonne quantité totale pour quatre types")
    void produire_quatreTypes_retourneTotalCorrect() {
        List<Fabricateur.Lunette> result = usine.produire(Map.of(
                Fabricateur.TypeLunette.CHATGPT, 2,
                Fabricateur.TypeLunette.BANANA, 1,
                Fabricateur.TypeLunette.CLAUDE, 2,
                Fabricateur.TypeLunette.LE_CHAT, 1
        ));
        assertEquals(6, result.size());
    }

    @Test
    @DisplayName("Doit enregistrer les serials produits")
    void produire_enregistreSerialsProduites() {
        usine.produire(Map.of(Fabricateur.TypeLunette.CLAUDE, 2));
        assertFalse(usine.getSerialsProduits().isEmpty());
        assertEquals(2, usine.getSerialsProduits().size());
    }

    @Test
    @DisplayName("Doit produire avec limite de capacité")
    void produire_depasseCapacite_fabriqueEnPlusieursLots() {
        when(fabricateurMock.getCapacity()).thenReturn(2);
        usine = new Usine(fabricateurMock);

        List<Fabricateur.Lunette> result = usine.produire(Map.of(
                Fabricateur.TypeLunette.CHATGPT, 5
        ));

        assertEquals(5, result.size());
        verify(fabricateurMock, atLeastOnce()).configurer(any());

        usine.shutdown();
    }

    @Test
    @DisplayName("Doit retourner une liste vide pour une commande vide")
    void produire_commandeVide_retourneListeVide() {
        List<Fabricateur.Lunette> result = usine.produire(Map.of());
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Doit produire la quantité maximale par type (9)")
    void produire_maxQuantiteParType_fabriqueCorrectement() {
        List<Fabricateur.Lunette> result = usine.produire(Map.of(
                Fabricateur.TypeLunette.BANANA, 9
        ));
        assertEquals(9, result.size());
    }

    @Test
    @DisplayName("Doit ajouter une commande mutualisée et retourner CompletableFuture")
    void ajouterCommandeMutualisee_commandeValide_retourneCompletableFuture() {
        CompletableFuture<List<Fabricateur.Lunette>> future = usine.ajouterCommandeMutualisee(
                "uuid-1",
                Map.of(Fabricateur.TypeLunette.CHATGPT, 2)
        );

        assertNotNull(future);
        assertFalse(future.isCompletedExceptionally());

        try {
            List<Fabricateur.Lunette> result = future.get();
            assertNotNull(result);
        } catch (InterruptedException | ExecutionException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Doit traiter plusieurs commandes mutualisées ensemble")
    void ajouterCommandeMutualisee_multipleCommandes_traiteEnsemble() throws InterruptedException {
        CompletableFuture<List<Fabricateur.Lunette>> future1 = usine.ajouterCommandeMutualisee(
                "uuid-1",
                Map.of(Fabricateur.TypeLunette.CHATGPT, 1)
        );

        CompletableFuture<List<Fabricateur.Lunette>> future2 = usine.ajouterCommandeMutualisee(
                "uuid-2",
                Map.of(Fabricateur.TypeLunette.CLAUDE, 1)
        );

        Thread.sleep(100);

        assertNotNull(future1);
        assertNotNull(future2);
    }

    @Test
    @DisplayName("Doit retourner une map vide quand pas de production")
    void getSerialsProduits_sansFabrication_retourneMapVide() {
        assertTrue(usine.getSerialsProduits().isEmpty());
    }

    @Test
    @DisplayName("Doit contenir tous les serials produits")
    void getSerialsProduits_apresProduction_contientTousLesSerials() {
        Map<Fabricateur.TypeLunette, Integer> commande = Map.of(
                Fabricateur.TypeLunette.BANANA, 3
        );
        usine.produire(commande);

        Map<String, Fabricateur.TypeLunette> serials = usine.getSerialsProduits();
        assertEquals(3, serials.size());

        serials.forEach((serial, type) -> {
            assertEquals(Fabricateur.TypeLunette.BANANA, type);
            assertTrue(serial.contains("BANANA"));
        });
    }

    @Test
    @DisplayName("produire() ne doit pas modifier getSerialsProduits() d'un appel précédent")
    void produire_deuxAppels_accumuleLesSerials() {
        usine.produire(Map.of(Fabricateur.TypeLunette.CHATGPT, 2));
        usine.produire(Map.of(Fabricateur.TypeLunette.BANANA, 1));
        assertEquals(3, usine.getSerialsProduits().size());
    }

    @Test
    @DisplayName("ajouterCommandeMutualisee() après shutdown() doit gérer l'erreur proprement")
    void ajouterCommandeMutualisee_apresShutdown_neBlockePas() {
        usine.shutdown();
        CompletableFuture<List<Fabricateur.Lunette>> future = usine.ajouterCommandeMutualisee(
                "uuid-1", Map.of(Fabricateur.TypeLunette.CHATGPT, 1)
        );
        assertNotNull(future);
    }
}
