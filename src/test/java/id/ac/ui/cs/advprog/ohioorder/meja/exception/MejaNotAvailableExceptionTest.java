package id.ac.ui.cs.advprog.ohioorder.meja.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaNotAvailableExceptionTest {

    @Test
    void testMejaNotAvailableExceptionMessage() {
        String errorMessage = "Meja tidak tersedia untuk pemesanan";
        MejaNotAvailableException exception = new MejaNotAvailableException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
    }
    
    @Test
    void testMejaNotAvailableExceptionInheritance() {
        MejaNotAvailableException exception = new MejaNotAvailableException("Test message");
        
        assertTrue(exception instanceof RuntimeException);
    }
}
