package id.ac.ui.cs.advprog.ohioorder.meja.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaNotFoundExceptionTest {

    @Test
    void testMejaNotFoundExceptionMessage() {
        String errorMessage = "Meja tidak ditemukan";
        MejaNotFoundException exception = new MejaNotFoundException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
    }
    
    @Test
    void testMejaNotFoundExceptionInheritance() {
        MejaNotFoundException exception = new MejaNotFoundException("Test message");
        
        assertTrue(exception instanceof RuntimeException);
    }
}
