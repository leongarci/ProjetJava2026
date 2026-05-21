package server;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import bernard_flou.Fabricateur;
import factory.Usine;
import protocol.CommandeSerializer;

@DisplayName("Suite de tests MosquittoBack Handler")
class MosquittoBackTest {

    private Usine usineMock;

    @BeforeEach
    void setUp() {
        usineMock = mock(Usine.class);
    }

    @Test
    @DisplayName("Un payload valide est bien désérialisé avant transmission à Usine")
    void payload_valide_deserialiseEtTransmisAUsine() throws Exception {
        String payload = "CHATGPT:2;CLAUDE:1;";
        Map<Fabricateur.TypeLunette, Integer> commande = CommandeSerializer.deserialize(payload);

        usineMock.valider(commande);
        usineMock.ajouterCommandeMutualisee("uuid-1", commande);

        verify(usineMock).valider(commande);
        verify(usineMock).ajouterCommandeMutualisee("uuid-1", commande);
    }

    @Test
    @DisplayName("Un payload invalide lève IllegalArgumentException lors de la validation")
    void payload_invalide_leveExceptionALaValidation() {
        Map<Fabricateur.TypeLunette, Integer> commandeInvalide
                = Map.of(Fabricateur.TypeLunette.CHATGPT, -1);

        doThrow(new IllegalArgumentException("Quantité invalide"))
                .when(usineMock).valider(commandeInvalide);

        assertThrows(IllegalArgumentException.class,
                () -> usineMock.valider(commandeInvalide));
    }

    @Test
    @DisplayName("Un serial connu dans getSerialsProduits retourne bien le type associé")
    void getSerialsProduits_serialConnu_retourneType() {
        when(usineMock.getSerialsProduits())
                .thenReturn(Map.of("SERIAL-001", Fabricateur.TypeLunette.BANANA));

        String type = String.valueOf(usineMock.getSerialsProduits().get("SERIAL-001"));
        assertEquals("BANANA", type);
    }

    @Test
    @DisplayName("Un serial inconnu dans getSerialsProduits retourne 'null' (converti en String)")
    void getSerialsProduits_serialInconnu_retourneNull() {
        when(usineMock.getSerialsProduits()).thenReturn(Map.of());

        String serial = String.valueOf(usineMock.getSerialsProduits().get("INCONNU"));
        assertEquals("null", serial);
    }
}
