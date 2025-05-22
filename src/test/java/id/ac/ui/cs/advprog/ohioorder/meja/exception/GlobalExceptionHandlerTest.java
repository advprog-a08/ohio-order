package id.ac.ui.cs.advprog.ohioorder.meja.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void testHandleMejaNotAvailableException() {
        String errorMessage = "Meja tidak tersedia untuk pemesanan";
        MejaNotAvailableException exception = new MejaNotAvailableException(errorMessage);
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity = 
                exceptionHandler.handleMejaNotAvailableException(exception);
        
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        
        GlobalExceptionHandler.ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.status());
        assertEquals(errorMessage, errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }
    
    @Test
    void testHandleMejaNotFoundException() {
        String errorMessage = "Meja dengan ID tersebut tidak ditemukan";
        MejaNotFoundException exception = new MejaNotFoundException(errorMessage);
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity = 
                exceptionHandler.handleMejaNotFoundException(exception);
        
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        
        GlobalExceptionHandler.ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.status());
        assertEquals(errorMessage, errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }
    
    @Test
    void testHandleMejaAlreadyExistsException() {
        String errorMessage = "Meja dengan nomor tersebut sudah ada";
        MejaAlreadyExistsException exception = new MejaAlreadyExistsException(errorMessage);
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity = 
                exceptionHandler.handleMejaAlreadyExistsException(exception);
        
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        
        GlobalExceptionHandler.ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.status());
        assertEquals(errorMessage, errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }
    
    @Test
    void testHandleMejaHasPesananException() {
        String errorMessage = "Meja tidak dapat dihapus karena memiliki pesanan aktif";
        MejaHasPesananException exception = new MejaHasPesananException(errorMessage);
        
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity = 
                exceptionHandler.handleMejaHasPesananException(exception);
        
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        
        GlobalExceptionHandler.ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.status());
        assertEquals(errorMessage, errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }
}
