package app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import protocol.CommandeListener;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Suite de tests MosquittoApp Frontend")
class MosquittoAppTest {

    @Test
    @DisplayName("setListener enregistre bien le listener")
    void setListener_listenerBienEnregistre() {
        MosquittoApp appMock = mock(MosquittoApp.class);
        CommandeListener listener = mock(CommandeListener.class);

        assertDoesNotThrow(() -> appMock.setListener(listener));
        verify(appMock).setListener(listener);
    }

    @Test
    @DisplayName("order() appelle bien la méthode avec le bon payload et uuid")
    void order_appelAvecBonsParametres() {
        MosquittoApp appMock = mock(MosquittoApp.class);

        String payload = "CHATGPT:2;CLAUDE:1;";
        String uuid = "order-123";

        assertDoesNotThrow(() -> appMock.order(payload, uuid));
        verify(appMock).order(payload, uuid);
    }

    @Test
    @DisplayName("askSerials() appelle bien la méthode avec le bon code")
    void askSerials_appelAvecBonCode() {
        MosquittoApp appMock = mock(MosquittoApp.class);

        String code = "SERIAL-ABC123";
        assertDoesNotThrow(() -> appMock.askSerials(code));
        verify(appMock).askSerials(code);
    }

    @Test
    @DisplayName("disconnect() peut être appelé sans exception")
    void disconnect_sansException() {
        MosquittoApp appMock = mock(MosquittoApp.class);
        assertDoesNotThrow(() -> appMock.disconnect());
        verify(appMock).disconnect();
    }
}
