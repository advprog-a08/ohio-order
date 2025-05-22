package id.ac.ui.cs.advprog.ohioorder.meja.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaAlreadyExistsExceptionTest {

    @Test
    void testMejaAlreadyExistsExceptionMessage() {
        String errorMessage = "Meja dengan nomor tersebut sudah ada";
        MejaAlreadyExistsException exception = new MejaAlreadyExistsException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
    }
    
    @Test
    void testMejaAlreadyExistsExceptionInheritance() {
        MejaAlreadyExistsException exception = new MejaAlreadyExistsException("Test message");
        
        assertTrue(exception instanceof RuntimeException);
    }
}
