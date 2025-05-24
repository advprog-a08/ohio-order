package id.ac.ui.cs.advprog.ohioorder.meja.validation;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.InvalidRequestException;
import id.ac.ui.cs.advprog.ohioorder.meja.repository.MejaRepository;
import id.ac.ui.cs.advprog.ohioorder.meja.utils.MejaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MejaRequestValidatorTest {

    @Mock
    private MejaRepository mejaRepository;

    @Mock
    private MejaConfig mejaConfig;

    @InjectMocks
    private MejaRequestValidator validator;

    private MejaRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = MejaRequest.builder()
                .nomorMeja("A1")
                .build();
    }

    @Test
    void testValidateWithValidRequest() {
        when(mejaConfig.isValidTableNumber("A1")).thenReturn(true);
        when(mejaRepository.existsByNomorMeja("A1")).thenReturn(false);

        assertDoesNotThrow(() -> validator.validate(validRequest));

        verify(mejaConfig).isValidTableNumber("A1");
        verify(mejaRepository).existsByNomorMeja("A1");
    }

    @Test
    void testValidateThrowsExceptionWhenNomorMejaIsNull() {
        MejaRequest requestWithNullNomor = MejaRequest.builder()
                .nomorMeja(null)
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validate(requestWithNullNomor)
        );

        assertEquals("Nomor meja tidak boleh kosong", exception.getMessage());
        verify(mejaConfig, never()).isValidTableNumber(anyString());
        verify(mejaRepository, never()).existsByNomorMeja(anyString());
    }

    @Test
    void testValidateThrowsExceptionWhenNomorMejaIsEmpty() {
        MejaRequest requestWithEmptyNomor = MejaRequest.builder()
                .nomorMeja("")
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validate(requestWithEmptyNomor)
        );

        assertEquals("Nomor meja tidak boleh kosong", exception.getMessage());
    }

    @Test
    void testValidateThrowsExceptionWhenNomorMejaIsWhitespace() {
        MejaRequest requestWithWhitespaceNomor = MejaRequest.builder()
                .nomorMeja("   ")
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validate(requestWithWhitespaceNomor)
        );

        assertEquals("Nomor meja tidak boleh kosong", exception.getMessage());
    }

    @Test
    void testValidateThrowsExceptionWhenNomorMejaFormatInvalid() {
        MejaRequest requestWithInvalidFormat = MejaRequest.builder()
                .nomorMeja("123")
                .build();

        when(mejaConfig.isValidTableNumber("123")).thenReturn(false);
        when(mejaConfig.getMaxTableCount()).thenReturn(100);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validate(requestWithInvalidFormat)
        );

        assertEquals("Nomor meja tidak valid. Format yang benar adalah *X, dimana * adalah alfabet A-Z dan X adalah angka antara 1 dan 100",
                exception.getMessage());
        verify(mejaConfig).isValidTableNumber("123");
        verify(mejaConfig).getMaxTableCount();
    }

    @Test
    void testValidateThrowsExceptionWhenNomorMejaAlreadyExists() {
        when(mejaConfig.isValidTableNumber("A1")).thenReturn(true);
        when(mejaRepository.existsByNomorMeja("A1")).thenReturn(true);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validate(validRequest)
        );

        assertEquals("Meja dengan nomor A1 sudah ada", exception.getMessage());
        verify(mejaRepository).existsByNomorMeja("A1");
    }

    @Test
    void testValidateForUpdateWithValidRequest() {
        when(mejaConfig.isValidTableNumber("A2")).thenReturn(true);
        when(mejaRepository.existsByNomorMeja("A2")).thenReturn(false);

        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();

        assertDoesNotThrow(() -> validator.validateForUpdate(updateRequest, "A1"));

        verify(mejaConfig).isValidTableNumber("A2");
        verify(mejaRepository).existsByNomorMeja("A2");
    }

    @Test
    void testValidateForUpdateWhenSameNomorMeja() {
        when(mejaConfig.isValidTableNumber("A1")).thenReturn(true);
        when(mejaRepository.existsByNomorMeja("A1")).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateForUpdate(validRequest, "A1"));

        verify(mejaConfig).isValidTableNumber("A1");
        verify(mejaRepository).existsByNomorMeja("A1");
    }

    @Test
    void testValidateForUpdateThrowsExceptionWhenNewNomorMejaExists() {
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();

        when(mejaConfig.isValidTableNumber("A2")).thenReturn(true);
        when(mejaRepository.existsByNomorMeja("A2")).thenReturn(true);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateForUpdate(updateRequest, "A1")
        );

        assertEquals("Meja dengan nomor A2 sudah ada", exception.getMessage());
    }

    @Test
    void testValidateForUpdateWithNullCurrentNomorMeja() {
        when(mejaConfig.isValidTableNumber("A1")).thenReturn(true);
        when(mejaRepository.existsByNomorMeja("A1")).thenReturn(true);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateForUpdate(validRequest, null)
        );

        assertEquals("Meja dengan nomor A1 sudah ada", exception.getMessage());
    }

    @Test
    void testValidateForUpdateThrowsExceptionWhenNomorMejaEmpty() {
        MejaRequest requestWithEmptyNomor = MejaRequest.builder()
                .nomorMeja("")
                .build();

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateForUpdate(requestWithEmptyNomor, "A1")
        );

        assertEquals("Nomor meja tidak boleh kosong", exception.getMessage());
    }

    @Test
    void testValidateForUpdateThrowsExceptionWhenFormatInvalid() {
        MejaRequest requestWithInvalidFormat = MejaRequest.builder()
                .nomorMeja("invalid")
                .build();

        when(mejaConfig.isValidTableNumber("invalid")).thenReturn(false);
        when(mejaConfig.getMaxTableCount()).thenReturn(100);

        InvalidRequestException exception = assertThrows(
                InvalidRequestException.class,
                () -> validator.validateForUpdate(requestWithInvalidFormat, "A1")
        );

        assertEquals("Nomor meja tidak valid. Format yang benar adalah *X, dimana * adalah alfabet A-Z dan X adalah angka antara 1 dan 100",
                exception.getMessage());
    }
}