package controller;

import app.MosquittoApp;
import com.sun.javafx.application.PlatformImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import protocol.CommandeListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@DisplayName("Suite de tests VerifierController")
class VerifierControllerTest {

    private VerifierController controller;
    private MosquittoApp mosquittoAppMock;

    @BeforeAll
    static void initJavaFX() {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        controller = new VerifierController();
        mosquittoAppMock = mock(MosquittoApp.class);
    }


    @Test
    @DisplayName("setMosquittoApp enregistre le contrôleur comme listener")
    void setMosquittoApp_registresAsListener() {
        assertDoesNotThrow(() -> controller.setMosquittoApp(mosquittoAppMock));
        verify(mosquittoAppMock).setListener(controller);
    }

    @Test
    @DisplayName("onChecked() ne lève pas d'exception avec des paramètres valides")
    void onChecked_parametresValides_sansException() {
        assertDoesNotThrow(() -> controller.onChecked("SERIAL123", "CHATGPT"));
    }

    @Test
    @DisplayName("onChecked() accepte tous les types de lunettes")
    void onChecked_tousLesTypes_sansException() {
        assertDoesNotThrow(() -> controller.onChecked("S001", "CHATGPT"));
        assertDoesNotThrow(() -> controller.onChecked("S002", "CLAUDE"));
        assertDoesNotThrow(() -> controller.onChecked("S003", "BANANA"));
        assertDoesNotThrow(() -> controller.onChecked("S004", "LE_CHAT"));
    }

    @Test
    @DisplayName("onChecked() accepte 'invalid' quand le serial n'existe pas")
    void onChecked_serialInvalide_accepteInvalid() {
        assertDoesNotThrow(() -> controller.onChecked("INCONNU", "invalid"));
    }

    @Test
    @DisplayName("setMosquittoApp() appelé deux fois remplace bien le listener")
    void setMosquittoApp_deuxFois_remplaceLancienListener() {
        MosquittoApp app1 = mock(MosquittoApp.class);
        MosquittoApp app2 = mock(MosquittoApp.class);

        controller.setMosquittoApp(app1);
        controller.setMosquittoApp(app2);

        verify(app1).setListener(controller);
        verify(app2).setListener(controller);
    }
}
