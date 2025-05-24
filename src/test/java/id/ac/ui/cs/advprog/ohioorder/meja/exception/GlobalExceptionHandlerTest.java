package id.ac.ui.cs.advprog.ohioorder.meja.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

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

    @Test
    void testHandleValidationExceptions() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testObject");

        bindingResult.addError(new FieldError("testObject", "nomorMeja", "Nomor meja tidak boleh kosong"));
        bindingResult.addError(new FieldError("testObject", "status", "Status tidak valid"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> responseEntity =
                exceptionHandler.handleValidationExceptions(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        Map<String, String> errors = responseEntity.getBody();
        assertNotNull(errors);
        assertEquals(2, errors.size());
        assertEquals("Nomor meja tidak boleh kosong", errors.get("nomorMeja"));
        assertEquals("Status tidak valid", errors.get("status"));
    }

    @Test
    void testHandleValidationExceptionsWithSingleError() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "testObject");

        bindingResult.addError(new FieldError("testObject", "nomorMeja", "Format nomor meja salah"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> responseEntity =
                exceptionHandler.handleValidationExceptions(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        Map<String, String> errors = responseEntity.getBody();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals("Format nomor meja salah", errors.get("nomorMeja"));
    }

    @Test
    void testHandleInvalidRequestException() {
        String errorMessage = "Request tidak valid";
        InvalidRequestException exception = new InvalidRequestException(errorMessage);

        ResponseEntity<GlobalExceptionHandler.ErrorResponse> responseEntity =
                exceptionHandler.handleInvalidRequestException(exception);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

        GlobalExceptionHandler.ErrorResponse errorResponse = responseEntity.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.status());
        assertEquals(errorMessage, errorResponse.message());
        assertNotNull(errorResponse.timestamp());
    }

}
