package protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Suite de tests Mosquitto Protocol")
class MosquittoProtocolTest {

    @Test
    @DisplayName("Le topic 'orders/' doit être défini comme constante")
    void topicNewOrder_estBienDefini() throws Exception {
        var field = protocol.Mosquitto.class.getDeclaredField("topicNewOrder");
        field.setAccessible(true);
        protocol.Mosquitto m = new protocol.Mosquitto() {};
        String value = (String) field.get(m);
        assertNotNull(value);
        assertFalse(value.isBlank());
        assertTrue(value.endsWith("/"), "Le topic doit se terminer par '/'");
    }

    @Test
    @DisplayName("Le topic 'serials/' doit être défini comme constante")
    void topicSerials_estBienDefini() throws Exception {
        var field = protocol.Mosquitto.class.getDeclaredField("topicSerials");
        field.setAccessible(true);
        protocol.Mosquitto m = new protocol.Mosquitto() {};
        String value = (String) field.get(m);
        assertNotNull(value);
        assertFalse(value.isBlank());
    }

    @Test
    @DisplayName("Le QoS doit être compris entre 0 et 2 inclus")
    void qos_valeurValide() throws Exception {
        var field = protocol.Mosquitto.class.getDeclaredField("qos");
        field.setAccessible(true);
        protocol.Mosquitto m = new protocol.Mosquitto() {};
        int qos = (int) field.get(m);
        assertTrue(qos >= 0 && qos <= 2, "QoS doit être 0, 1 ou 2 — valeur actuelle : " + qos);
    }

    @Test
    @DisplayName("topicNewOrder + uuid doit former un topic valide")
    void topicNewOrder_concatenationUuid_valide() throws Exception {
        var field = protocol.Mosquitto.class.getDeclaredField("topicNewOrder");
        field.setAccessible(true);
        protocol.Mosquitto m = new protocol.Mosquitto() {};
        String base = (String) field.get(m);
        String uuid = "test-uuid-123";
        String full = base + uuid;
        assertTrue(full.contains("/"), "Le topic complet doit contenir '/'");
        assertTrue(full.endsWith(uuid), "Le topic doit se terminer par l'uuid");
    }

    @Test
    @DisplayName("topicSerials + uuid + '/check' doit former un topic de vérification valide")
    void topicSerials_checkTopic_valide() throws Exception {
        var field = protocol.Mosquitto.class.getDeclaredField("topicSerials");
        field.setAccessible(true);
        protocol.Mosquitto m = new protocol.Mosquitto() {};
        String base = (String) field.get(m);
        String uuid = "order-uuid";
        String checkTopic = base + uuid + "/check";
        assertTrue(checkTopic.contains(uuid));
        assertTrue(checkTopic.endsWith("/check"));
    }
}
