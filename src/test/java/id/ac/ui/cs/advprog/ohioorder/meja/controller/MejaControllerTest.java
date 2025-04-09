package id.ac.ui.cs.advprog.ohioorder.meja.controller;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MejaControllerTest {

    @Mock
    private MejaService mejaService;

    @InjectMocks
    private MejaController mejaController;

    private UUID uuid;
    private MejaRequest mejaRequest;
    private MejaResponse mejaResponse;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();
        
        mejaRequest = MejaRequest.builder()
                .nomorMeja("A1")
                .build();
        
        mejaResponse = MejaResponse.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();
    }

    @Test
    void testCreateMejaReturnsCreatedMejaResponse() {
        when(mejaService.createMeja(any(MejaRequest.class))).thenReturn(mejaResponse);

        ResponseEntity<MejaResponse> responseEntity = mejaController.createMeja(mejaRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(mejaResponse, responseEntity.getBody());
        
        verify(mejaService).createMeja(mejaRequest);
    }

    @Test
    void testGetAllMejaReturnsAllMejaResponses() {
        MejaResponse mejaResponse2 = MejaResponse.builder()
                .id(UUID.randomUUID())
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        List<MejaResponse> expectedMejaResponses = Arrays.asList(mejaResponse, mejaResponse2);
        
        when(mejaService.getAllMeja()).thenReturn(expectedMejaResponses);

        ResponseEntity<List<MejaResponse>> responseEntity = mejaController.getAllMeja();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedMejaResponses, responseEntity.getBody());
        assertEquals(2, responseEntity.getBody().size());
        
        verify(mejaService).getAllMeja();
    }

    @Test
    void testGetMejaByIdReturnsMejaResponse() {
        when(mejaService.getMejaById(uuid)).thenReturn(mejaResponse);

        ResponseEntity<MejaResponse> responseEntity = mejaController.getMejaById(uuid);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mejaResponse, responseEntity.getBody());
        
        verify(mejaService).getMejaById(uuid);
    }

    @Test
    void testUpdateMejaReturnsUpdatedMejaResponse() {
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();
        
        MejaResponse updatedResponse = MejaResponse.builder()
                .id(uuid)
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        when(mejaService.updateMeja(eq(uuid), any(MejaRequest.class))).thenReturn(updatedResponse);

        ResponseEntity<MejaResponse> responseEntity = mejaController.updateMeja(uuid, updateRequest);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedResponse, responseEntity.getBody());
        
        verify(mejaService).updateMeja(uuid, updateRequest);
    }

    @Test
    void testDeleteMejaReturnsNoContent() {
        doNothing().when(mejaService).deleteMeja(uuid);

        ResponseEntity<Void> responseEntity = mejaController.deleteMeja(uuid);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        
        verify(mejaService).deleteMeja(uuid);
    }

    @Test
    void testGetMejaByNomorMejaReturnsMejaResponse() {
        when(mejaService.getMejaByNomorMeja("A1")).thenReturn(mejaResponse);

        ResponseEntity<MejaResponse> responseEntity = mejaController.getMejaByNomorMeja("A1");

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mejaResponse, responseEntity.getBody());
        
        verify(mejaService).getMejaByNomorMeja("A1");
    }

    @Test
    void testSetMejaStatusReturnsUpdatedMejaResponse() {
        MejaResponse updatedResponse = MejaResponse.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();
        
        when(mejaService.setMejaStatus(uuid, MejaStatus.TERISI)).thenReturn(updatedResponse);

        ResponseEntity<MejaResponse> responseEntity = mejaController.setMejaStatus(uuid, MejaStatus.TERISI);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedResponse, responseEntity.getBody());
        
        verify(mejaService).setMejaStatus(uuid, MejaStatus.TERISI);
    }

    @Test
    void testIsMejaAvailableReturnsAvailabilityStatus() {
        when(mejaService.isMejaAvailable(uuid)).thenReturn(true);

        ResponseEntity<Boolean> responseEntity = mejaController.isMejaAvailable(uuid);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(true, responseEntity.getBody());
        
        verify(mejaService).isMejaAvailable(uuid);
    }

    @Test
    void testGetAvailableMejaReturnsAvailableMejaList() {
        List<MejaResponse> availableMejaList = Arrays.asList(mejaResponse);
        when(mejaService.getAvailableMeja()).thenReturn(availableMejaList);

        ResponseEntity<List<MejaResponse>> responseEntity = mejaController.getAvailableMeja();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(availableMejaList, responseEntity.getBody());
        assertEquals(1, responseEntity.getBody().size());
        
        verify(mejaService).getAvailableMeja();
    }
}