package protocol;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import bernard_flou.Fabricateur;

@DisplayName("Suite de tests LivraisonSerializer")
class LivraisonSerializerTest {

    @Test
    @DisplayName("Doit désérialiser quatre types correctement")
    void deserialize_quatreTypes_retourneListeComplete() {
        String payload = "CHATGPT:S001;BANANA:S002;CLAUDE:S003;LE_CHAT:S004;";
        List<String> result = LivraisonSerializer.deserialize(payload);
        assertEquals(4, result.size());
        assertEquals("Type: CHATGPT | Série: S001", result.get(0));
        assertEquals("Type: BANANA | Série: S002", result.get(1));
        assertEquals("Type: CLAUDE | Série: S003", result.get(2));
        assertEquals("Type: LE_CHAT | Série: S004", result.get(3));
    }

    @Test
    @DisplayName("Doit retourner une liste vide pour un payload vide")
    void deserialize_payloadVide_retourneListeVide() {
        List<String> result = LivraisonSerializer.deserialize("");
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Doit désérialiser un seul élément correctement")
    void deserialize_unSeulElement_leChat() {
        String payload = "LE_CHAT:SER999;";
        List<String> result = LivraisonSerializer.deserialize(payload);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Type: LE_CHAT | Série: SER999", result.getFirst());
    }

    @Test
    @DisplayName("Doit désérialiser banana correctement")
    void deserialize_banana_formatCorrect() {
        String payload = "BANANA:BAN001;";
        List<String> result = LivraisonSerializer.deserialize(payload);
        assertEquals("Type: BANANA | Série: BAN001", result.getFirst());
    }

    @Test
    @DisplayName("Doit sérialiser une liste de lunettes correctement")
    void serialize_lunettes_formatCorrect() {
        List<Fabricateur.Lunette> lunettes = new ArrayList<>();

        Fabricateur.Lunette l1 = (new Fabricateur.Lunette(Fabricateur.TypeLunette.CHATGPT, "S001"));
        Fabricateur.Lunette l2 = (new Fabricateur.Lunette(Fabricateur.TypeLunette.CLAUDE, "S002"));

        lunettes.add(l1);
        lunettes.add(l2);

        String result = LivraisonSerializer.serialize(lunettes);

        assertEquals("CHATGPT:S001;CLAUDE:S002;", result);
    }

    @Test
    @DisplayName("Doit sérialiser une liste vide correctement")
    void serialize_emptyList_retourneChainVide() {
        List<Fabricateur.Lunette> lunettes = new ArrayList<>();
        String result = LivraisonSerializer.serialize(lunettes);
        assertEquals("", result);
    }

    @Test
    @DisplayName("Doit désérialiser en ignorant les parties blanches")
    void deserialize_withBlankParts_ignoresBlankParts() {
        String payload = "CHATGPT:S001;;CLAUDE:S002;";
        List<String> result = LivraisonSerializer.deserialize(payload);
        assertEquals(2, result.size());
        assertEquals("Type: CHATGPT | Série: S001", result.get(0));
        assertEquals("Type: CLAUDE | Série: S002", result.get(1));
    }

    @Test
    @DisplayName("Doit gérer un serial avec des caractères spéciaux")
    void deserialize_serialWithSpecialChars_retourneFormatCorrect() {
        String payload = "LE_CHAT:SER-123-ABC;";
        List<String> result = LivraisonSerializer.deserialize(payload);
        assertEquals("Type: LE_CHAT | Série: SER-123-ABC", result.get(0));
    }

    @Test
    @DisplayName("Doit sérialiser et maintenir l'ordre")
    void serialize_multipleLunettes_maintainOrder() {
        List<Fabricateur.Lunette> lunettes = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Fabricateur.Lunette l = (new Fabricateur.Lunette(Fabricateur.TypeLunette.BANANA, "BAN" + i));
            lunettes.add(l);
        }

        String result = LivraisonSerializer.serialize(lunettes);

        assertTrue(result.contains("BANANA:BAN0;"));
        assertTrue(result.contains("BANANA:BAN1;"));
        assertTrue(result.contains("BANANA:BAN2;"));
    }

    @Test
    @DisplayName("serialize() puis deserialize() est idempotent")
    void serialize_puisDeserialize_estIdempotent() {
        List<Fabricateur.Lunette> lunettes = List.of(
                new Fabricateur.Lunette(Fabricateur.TypeLunette.CHATGPT, "S001"),
                new Fabricateur.Lunette(Fabricateur.TypeLunette.BANANA, "S002")
        );
        String serialized = LivraisonSerializer.serialize(lunettes);
        List<String> result = LivraisonSerializer.deserialize(serialized);
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("CHATGPT"));
        assertTrue(result.get(1).contains("BANANA"));
    }
}
