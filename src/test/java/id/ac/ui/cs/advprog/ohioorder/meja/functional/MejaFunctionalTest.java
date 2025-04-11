package id.ac.ui.cs.advprog.ohioorder.meja.functional;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaAlreadyExistsException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaHasPesananException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaNotFoundException;
import id.ac.ui.cs.advprog.ohioorder.meja.repository.MejaRepository;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MejaFunctionalTest {

    @Autowired
    private MejaService mejaService;

    @Autowired
    private MejaRepository mejaRepository;

    @BeforeEach
    void setUp() {
        mejaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        mejaRepository.deleteAll();
    }

    @Test
    void testFullCRUDLifecycle() {
        MejaRequest createRequest = MejaRequest.builder()
                .nomorMeja("T1")
                .build();
        
        MejaResponse createdResponse = mejaService.createMeja(createRequest);
        
        assertNotNull(createdResponse);
        assertNotNull(createdResponse.getId());
        assertEquals("T1", createdResponse.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA, createdResponse.getStatus());
        
        MejaResponse retrievedResponse = mejaService.getMejaById(createdResponse.getId());
        
        assertEquals(createdResponse.getId(), retrievedResponse.getId());
        assertEquals(createdResponse.getNomorMeja(), retrievedResponse.getNomorMeja());
        assertEquals(createdResponse.getStatus(), retrievedResponse.getStatus());
        
        MejaResponse retrievedByNomorResponse = mejaService.getMejaByNomorMeja("T1");
        
        assertEquals(createdResponse.getId(), retrievedByNomorResponse.getId());
        
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("T2")
                .build();
        
        MejaResponse updatedResponse = mejaService.updateMeja(createdResponse.getId(), updateRequest);
        
        assertEquals(createdResponse.getId(), updatedResponse.getId());
        assertEquals("T2", updatedResponse.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA, updatedResponse.getStatus());
        
        MejaResponse occupiedResponse = mejaService.setMejaStatus(createdResponse.getId(), MejaStatus.TERISI);
        
        assertEquals(createdResponse.getId(), occupiedResponse.getId());
        assertEquals("T2", occupiedResponse.getNomorMeja());
        assertEquals(MejaStatus.TERISI, occupiedResponse.getStatus());
        
        assertFalse(mejaService.isMejaAvailable(createdResponse.getId()));
        
        assertThrows(MejaHasPesananException.class, () -> {
            mejaService.deleteMeja(createdResponse.getId());
        });
        
        MejaResponse availableResponse = mejaService.setMejaStatus(createdResponse.getId(), MejaStatus.TERSEDIA);
        
        assertEquals(MejaStatus.TERSEDIA, availableResponse.getStatus());
        assertTrue(mejaService.isMejaAvailable(createdResponse.getId()));
        
        mejaService.deleteMeja(createdResponse.getId());
        
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaById(createdResponse.getId());
        });
    }

    @Test
    void testMultipleTablesAndAvailabilityFiltering() {
        MejaRequest request1 = MejaRequest.builder().nomorMeja("T1").build();
        MejaRequest request2 = MejaRequest.builder().nomorMeja("T2").build();
        MejaRequest request3 = MejaRequest.builder().nomorMeja("T3").build();
        
        MejaResponse response1 = mejaService.createMeja(request1);
        MejaResponse response2 = mejaService.createMeja(request2);
        MejaResponse response3 = mejaService.createMeja(request3);
        
        List<MejaResponse> allTables = mejaService.getAllMeja();
        assertEquals(3, allTables.size());
        
        mejaService.setMejaStatus(response1.getId(), MejaStatus.TERISI);
        mejaService.setMejaStatus(response3.getId(), MejaStatus.TERISI);
        
        List<MejaResponse> availableTables = mejaService.getAvailableMeja();
        assertEquals(1, availableTables.size());
        assertEquals(response2.getId(), availableTables.get(0).getId());
        
        assertFalse(mejaService.isMejaAvailable(response1.getId()));
        assertTrue(mejaService.isMejaAvailable(response2.getId()));
        assertFalse(mejaService.isMejaAvailable(response3.getId()));
    }

    @Test
    void testValidationRules() {
        MejaRequest request = MejaRequest.builder().nomorMeja("T1").build();
        mejaService.createMeja(request);
        
        MejaRequest duplicateRequest = MejaRequest.builder().nomorMeja("T1").build();
        assertThrows(MejaAlreadyExistsException.class, () -> {
            mejaService.createMeja(duplicateRequest);
        });
        
        MejaRequest request2 = MejaRequest.builder().nomorMeja("T2").build();
        MejaResponse response2 = mejaService.createMeja(request2);
        
        MejaRequest conflictingUpdateRequest = MejaRequest.builder().nomorMeja("T1").build();
        assertThrows(MejaAlreadyExistsException.class, () -> {
            mejaService.updateMeja(response2.getId(), conflictingUpdateRequest);
        });
        
        UUID nonExistentId = UUID.randomUUID();
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaById(nonExistentId);
        });
        
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaByNomorMeja("T999");
        });
        
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.deleteMeja(nonExistentId);
        });
    }

    @Test
    void testTableLifecycleWithPesanan() {
        MejaRequest request = MejaRequest.builder().nomorMeja("T1").build();
        MejaResponse response = mejaService.createMeja(request);
        
        assertTrue(mejaService.isMejaAvailable(response.getId()));
        
        mejaService.setMejaStatus(response.getId(), MejaStatus.TERISI);
        assertFalse(mejaService.isMejaAvailable(response.getId()));
        
        assertThrows(MejaHasPesananException.class, () -> {
            mejaService.deleteMeja(response.getId());
        });
        
        mejaService.setMejaStatus(response.getId(), MejaStatus.TERSEDIA);
        assertTrue(mejaService.isMejaAvailable(response.getId()));
        
        mejaService.deleteMeja(response.getId());
        
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaById(response.getId());
        });
    }
    
    @Test
    void testEdgeCasesAndStressTest() {
        for (int i = 1; i <= 10; i++) {
            MejaRequest request = MejaRequest.builder().nomorMeja("T" + i).build();
            mejaService.createMeja(request);
        }
        
        List<MejaResponse> allTables = mejaService.getAllMeja();
        assertEquals(10, allTables.size());
        
        for (MejaResponse table : allTables) {
            int tableNumber = Integer.parseInt(table.getNomorMeja().substring(1));
            if (tableNumber % 2 == 0) {
                mejaService.setMejaStatus(table.getId(), MejaStatus.TERISI);
            }
        }
        
        List<MejaResponse> availableTables = mejaService.getAvailableMeja();
        assertEquals(5, availableTables.size());
        
        for (MejaResponse table : allTables) {
            String currentNumber = table.getNomorMeja().substring(1);
            MejaRequest updateRequest = MejaRequest.builder().nomorMeja("A" + currentNumber).build();
            mejaService.updateMeja(table.getId(), updateRequest);
        }
        
        allTables = mejaService.getAllMeja();
        for (MejaResponse table : allTables) {
            assertTrue(table.getNomorMeja().startsWith("A"));
        }
        
        availableTables = mejaService.getAvailableMeja();
        for (MejaResponse table : availableTables) {
            mejaService.deleteMeja(table.getId());
        }
        
        allTables = mejaService.getAllMeja();
        assertEquals(5, allTables.size());
        for (MejaResponse table : allTables) {
            assertEquals(MejaStatus.TERISI, table.getStatus());
        }
    }
}