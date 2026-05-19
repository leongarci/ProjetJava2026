package factory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bernard_flou.Fabricateur;
import protocol.CommandeSerializer;
import protocol.LivraisonSerializer;

class CommandProcessingIntegrationTest {

    private Fabricateur fabricateurMock;
    private final AtomicInteger counter = new AtomicInteger(0);
    private Usine usine;

    @BeforeEach
    void setUp() {
        fabricateurMock = mock(Fabricateur.class);
        when(fabricateurMock.getCapacity()).thenReturn(10);

        when(fabricateurMock.fabriquer(Fabricateur.TypeLunette.CHATGPT))
                .thenAnswer(inv -> createMockLunette(Fabricateur.TypeLunette.CHATGPT,
                        "CHATGPT-S" + counter.getAndIncrement()));

        when(fabricateurMock.fabriquer(Fabricateur.TypeLunette.BANANA))
                .thenAnswer(inv -> createMockLunette(Fabricateur.TypeLunette.BANANA,
                        "BANANA-S" + counter.getAndIncrement()));

        when(fabricateurMock.fabriquer(Fabricateur.TypeLunette.CLAUDE))
                .thenAnswer(inv -> createMockLunette(Fabricateur.TypeLunette.CLAUDE,
                        "CLAUDE-S" + counter.getAndIncrement()));

        when(fabricateurMock.fabriquer(Fabricateur.TypeLunette.LE_CHAT))
                .thenAnswer(inv -> createMockLunette(Fabricateur.TypeLunette.LE_CHAT,
                        "LECHAT-S" + counter.getAndIncrement()));

        usine = new Usine(fabricateurMock);
    }

    @AfterEach
    void tearDown() {
        usine.shutdown();
        counter.set(0);
    }

    private Fabricateur.Lunette createMockLunette(Fabricateur.TypeLunette type, String serial) {
        Fabricateur.Lunette l = spy(new Fabricateur.Lunette(type, serial));
        return l;
    }

    @Test
    @DisplayName("Flux complet : Sérialiser commande -> Produire -> Sérialiser livraison")
    void fullCommandFlow_endToEnd() {
        Map<Fabricateur.TypeLunette, Integer> originalCommand = new HashMap<>();
        originalCommand.put(Fabricateur.TypeLunette.CHATGPT, 2);
        originalCommand.put(Fabricateur.TypeLunette.CLAUDE, 1);

        assertDoesNotThrow(() -> usine.valider(originalCommand));

        String serializedCommand = CommandeSerializer.serialize(originalCommand);
        assertFalse(serializedCommand.isEmpty());
        assertTrue(serializedCommand.contains("CHATGPT:2;"));
        assertTrue(serializedCommand.contains("CLAUDE:1;"));

        List<Fabricateur.Lunette> producedLunettes = usine.produire(originalCommand);
        assertEquals(3, producedLunettes.size());

        String serializedDelivery = LivraisonSerializer.serialize(producedLunettes);
        assertFalse(serializedDelivery.isEmpty());

        List<String> deserializedDelivery = LivraisonSerializer.deserialize(serializedDelivery);
        assertEquals(3, deserializedDelivery.size());
    }

    @Test
    @DisplayName("Validation et production de commande avec plusieurs types")
    void commandValidationAndProduction_withMultipleTypes() {
        Map<Fabricateur.TypeLunette, Integer> command = Map.of(
                Fabricateur.TypeLunette.CHATGPT, 3,
                Fabricateur.TypeLunette.BANANA, 2,
                Fabricateur.TypeLunette.CLAUDE, 1,
                Fabricateur.TypeLunette.LE_CHAT, 2
        );

        assertDoesNotThrow(() -> usine.valider(command));

        List<Fabricateur.Lunette> result = usine.produire(command);
        assertEquals(8, result.size());

        String serialized = LivraisonSerializer.serialize(result);
        List<String> deserialized = LivraisonSerializer.deserialize(serialized);
        assertEquals(8, deserialized.size());
    }

    @Test
    @DisplayName("Une commande invalide doit échouer la validation")
    void invalidCommand_shouldFailValidation() {
        Map<Fabricateur.TypeLunette, Integer> invalidCommand = new HashMap<>();
        invalidCommand.put(Fabricateur.TypeLunette.CHATGPT, -1);

        assertThrows(IllegalArgumentException.class, () -> usine.valider(invalidCommand));
    }

    @Test
    @DisplayName("Une commande sérialisée invalide doit échouer la désérialisation")
    void invalidSerializedCommand_shouldFailDeserialization() {
        String invalidPayload = "INVALID:DATA";

        assertThrows(IllegalArgumentException.class, () ->
                CommandeSerializer.deserialize(invalidPayload));
    }

    @Test
    @DisplayName("Cycle : Sérialiser -> Désérialiser commande")
    void commandRoundtrip_shouldMaintainData() {
        Map<Fabricateur.TypeLunette, Integer> original = Map.of(
                Fabricateur.TypeLunette.CHATGPT, 2,
                Fabricateur.TypeLunette.CLAUDE, 1
        );

        String serialized = CommandeSerializer.serialize(original);
        Map<Fabricateur.TypeLunette, Integer> deserialized = CommandeSerializer.deserialize(serialized);

        assertEquals(original, deserialized);
    }

    @Test
    @DisplayName("Suivi des serials : Tous les serials produits doivent être suivis")
    void serialTracking_allProducedUnitsShouldBeTracked() {
        Map<Fabricateur.TypeLunette, Integer> command = Map.of(
                Fabricateur.TypeLunette.CHATGPT, 3,
                Fabricateur.TypeLunette.BANANA, 2
        );

        usine.produire(command);

        Map<String, Fabricateur.TypeLunette> trackedSerials = usine.getSerialsProduits();
        assertEquals(5, trackedSerials.size());

        trackedSerials.forEach((serial, type) ->
                assertTrue(
                        type == Fabricateur.TypeLunette.CHATGPT ||
                                type == Fabricateur.TypeLunette.BANANA,
                        "Serial should be for CHATGPT or BANANA"
                ));
    }

    @Test
    @DisplayName("Limites de capacité : Doit gérer la production au-delà de la capacité")
    void capacityLimits_shouldHandleProductionBeyondCapacity() {
        when(fabricateurMock.getCapacity()).thenReturn(2);
        usine = new Usine(fabricateurMock);

        Map<Fabricateur.TypeLunette, Integer> command = Map.of(
                Fabricateur.TypeLunette.CLAUDE, 5
        );

        List<Fabricateur.Lunette> result = usine.produire(command);

        assertEquals(5, result.size());
        verify(fabricateurMock, atLeastOnce()).configurer(any());

        usine.shutdown();
    }
}
