package protocol;

import bernard_flou.Fabricateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suite de tests CommandeSerializer")
class CommandeSerializerTest {

    private Map<Fabricateur.TypeLunette, Integer> commande;

    @BeforeEach
    void setUp() {
        commande = new HashMap<>();
    }

    @Test
    @DisplayName("Doit sérialiser un seul type correctement")
    void serialize_unType_formatCorrect() {
        Map<Fabricateur.TypeLunette, Integer> commande = Map.of(
                Fabricateur.TypeLunette.CHATGPT, 3
        );
        String result = CommandeSerializer.serialize(commande);
        assertEquals("CHATGPT:3;", result);
    }

    @Test
    @DisplayName("Doit désérialiser deux types correctement")
    void deserialize_deuxTypes_retourneMapCorrect() {
        String payload = "CHATGPT:3;CLAUDE:2;";
        Map<Fabricateur.TypeLunette, Integer> result = CommandeSerializer.deserialize(payload);
        assertEquals(3, result.get(Fabricateur.TypeLunette.CHATGPT));
        assertEquals(2, result.get(Fabricateur.TypeLunette.CLAUDE));
    }

    @Test
    @DisplayName("Doit désérialiser quatre types correctement")
    void deserialize_quatreTypes_retourneTousLesTypes() {
        String payload = "CHATGPT:1;BANANA:2;CLAUDE:3;LE_CHAT:4;";
        Map<Fabricateur.TypeLunette, Integer> result = CommandeSerializer.deserialize(payload);
        assertEquals(1, result.get(Fabricateur.TypeLunette.CHATGPT));
        assertEquals(2, result.get(Fabricateur.TypeLunette.BANANA));
        assertEquals(3, result.get(Fabricateur.TypeLunette.CLAUDE));
        assertEquals(4, result.get(Fabricateur.TypeLunette.LE_CHAT));
    }

    @Test
    @DisplayName("Doit lever une exception pour un format invalide")
    void deserialize_formatInvalide_leveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> CommandeSerializer.deserialize("formatInvalide"));
    }

    @Test
    @DisplayName("Doit lever une exception pour une chaîne vide")
    void deserialize_chaineVide_leveIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> CommandeSerializer.deserialize(""));
    }

    @Test
    @DisplayName("Doit être idempotent pour sérialiser/désérialiser")
    void serialize_puisDeserialize_estIdempotent() {
        Map<Fabricateur.TypeLunette, Integer> original = Map.of(
                Fabricateur.TypeLunette.CHATGPT, 2,
                Fabricateur.TypeLunette.BANANA, 1,
                Fabricateur.TypeLunette.CLAUDE, 3,
                Fabricateur.TypeLunette.LE_CHAT, 1
        );
        String serialized = CommandeSerializer.serialize(original);
        Map<Fabricateur.TypeLunette, Integer> result = CommandeSerializer.deserialize(serialized);
        assertEquals(original, result);
    }

    @Test
    @DisplayName("Doit sérialiser une commande vide")
    void serialize_commandeVide_retourneChaine() {
        String result = CommandeSerializer.serialize(commande);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Doit lever une exception pour un nom de type invalide")
    void deserialize_typeInvalide_leveException() {
        String invalidPayload = "INVALID_TYPE:5;";
        assertThrows(IllegalArgumentException.class, () ->
                CommandeSerializer.deserialize(invalidPayload)
        );
    }

    @Test
    @DisplayName("Doit lever une exception si la quantité n'est pas numérique")
    void deserialize_quantiteNonNumerique_leveException() {
        assertThrows(IllegalArgumentException.class, () ->
                CommandeSerializer.deserialize("CHATGPT:abc;"));
    }

    @Test
    @DisplayName("Doit désérialiser la quantité zéro")
    void deserialize_quantiteZero_retourneZero() {
        String payload = "CHATGPT:0;";
        Map<Fabricateur.TypeLunette, Integer> result = CommandeSerializer.deserialize(payload);
        assertEquals(0, result.get(Fabricateur.TypeLunette.CHATGPT));
    }

    @Test
    @DisplayName("Doit lever une exception si les deux points manquent")
    void deserialize_missingSeparator_leveException() {
        String invalidPayload = "CHATGPT2;";
        assertThrows(IllegalArgumentException.class, () ->
                CommandeSerializer.deserialize(invalidPayload)
        );
    }

    @Test
    @DisplayName("Doit lever une exception si la chaîne contient des parties blanches (;;)")
    void deserialize_withBlankParts_leveExceptionSiDoublePointVirgule() {
        assertThrows(IllegalArgumentException.class, () ->
                CommandeSerializer.deserialize("CHATGPT:2;;CLAUDE:1;"));
    }

    @Test
    @DisplayName("Doit sérialiser avec la quantité maximale (9)")
    void serialize_maxQuantity_retourneFormatCorrect() {
        Map<Fabricateur.TypeLunette, Integer> commande = Map.of(
                Fabricateur.TypeLunette.BANANA, 9
        );
        String result = CommandeSerializer.serialize(commande);
        assertEquals("BANANA:9;", result);
    }
}