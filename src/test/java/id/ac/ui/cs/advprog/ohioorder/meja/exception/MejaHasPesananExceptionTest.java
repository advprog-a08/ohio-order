package id.ac.ui.cs.advprog.ohioorder.meja.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaHasPesananExceptionTest {

    @Test
    void testMejaHasPesananExceptionMessage() {
        String errorMessage = "Meja tidak dapat dihapus karena memiliki pesanan aktif";
        MejaHasPesananException exception = new MejaHasPesananException(errorMessage);
        
        assertEquals(errorMessage, exception.getMessage());
    }
    
    @Test
    void testMejaHasPesananExceptionInheritance() {
        MejaHasPesananException exception = new MejaHasPesananException("Test message");
        
        assertTrue(exception instanceof RuntimeException);
    }
}
