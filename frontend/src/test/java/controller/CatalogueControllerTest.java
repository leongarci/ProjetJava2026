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

@DisplayName("Suite de tests CatalogueController")
class CatalogueControllerTest {

    private CatalogueController controller;
    private MosquittoApp mosquittoAppMock;



    @BeforeAll
    static void initJavaFX() {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        controller = new CatalogueController();
        mosquittoAppMock = mock(MosquittoApp.class);
    }


    @Test
    @DisplayName("Doit définir MosquittoApp et s'enregistrer comme listener")
    void setMosquittoApp_registersAsListener() {
        assertDoesNotThrow(() -> controller.setMosquittoApp(mosquittoAppMock));
        verify(mosquittoAppMock).setListener(controller);
    }

    @Test
    @DisplayName("Doit gérer le callback onValidated")
    void onValidated_handleCallback() {
        assertDoesNotThrow(() -> controller.onValidated("order-123"));
    }

    @Test
    @DisplayName("Doit gérer le callback onCancelled")
    void onCancelled_handleCallback() {
        assertDoesNotThrow(() -> controller.onCancelled("order-123", "Invalid format"));
    }

    @Test
    @DisplayName("Doit gérer le callback onError")
    void onError_handleCallback() {
        assertDoesNotThrow(() -> controller.onError("order-123", "Connection error"));
    }
}


