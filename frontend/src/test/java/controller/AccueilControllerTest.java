package controller;

import app.MosquittoApp;
import com.sun.javafx.application.PlatformImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Suite de tests AccueilController")
class AccueilControllerTest {

    private AccueilController controller;
    private MosquittoApp mosquittoAppMock;

    @BeforeAll
    static void initJavaFX() {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        controller = new AccueilController();
        mosquittoAppMock = mock(MosquittoApp.class);
    }


    @Test
    @DisplayName("setMosquittoApp accepte une instance valide sans exception")
    void setMosquittoApp_instanceValide_sansException() {
        assertDoesNotThrow(() -> controller.setMosquittoApp(mosquittoAppMock));
    }

    @Test
    @DisplayName("setMosquittoApp avec une instance valide stocke l'app (pas d'interaction inattendue)")
    void setMosquittoApp_instanceValide_aucuneInteractionSurApp() {
        controller.setMosquittoApp(mosquittoAppMock);
        verifyNoInteractions(mosquittoAppMock);
    }
}
