package id.ac.ui.cs.advprog.ohioorder.meja.dto;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MejaResponseTest {

    @Test
    void testMejaResponseBuilder() {
        UUID id = UUID.randomUUID();
        String nomorMeja = "A1";
        MejaStatus status = MejaStatus.TERSEDIA;
        
        MejaResponse response = MejaResponse.builder()
                .id(id)
                .nomorMeja(nomorMeja)
                .status(status)
                .build();
        
        assertEquals(id, response.getId());
        assertEquals(nomorMeja, response.getNomorMeja());
        assertEquals(status, response.getStatus());
    }
    
    @Test
    void testNoArgsConstructor() {
        MejaResponse response = new MejaResponse();
        
        assertNull(response.getId());
        assertNull(response.getNomorMeja());
        assertNull(response.getStatus());
    }
    
    @Test
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        String nomorMeja = "B2";
        MejaStatus status = MejaStatus.TERISI;
        
        MejaResponse response = new MejaResponse(id, nomorMeja, status);
        
        assertEquals(id, response.getId());
        assertEquals(nomorMeja, response.getNomorMeja());
        assertEquals(status, response.getStatus());
    }
    
    @Test
    void testSettersAndGetters() {
        MejaResponse response = new MejaResponse();
        
        UUID id = UUID.randomUUID();
        String nomorMeja = "C3";
        MejaStatus status = MejaStatus.TERSEDIA;
        
        response.setId(id);
        response.setNomorMeja(nomorMeja);
        response.setStatus(status);
        
        assertEquals(id, response.getId());
        assertEquals(nomorMeja, response.getNomorMeja());
        assertEquals(status, response.getStatus());
    }
    
    @Test
    void testEqualsAndHashCode() {
        UUID id = UUID.randomUUID();
        String nomorMeja = "D4";
        MejaStatus status = MejaStatus.TERSEDIA;
        
        MejaResponse response1 = MejaResponse.builder()
                .id(id)
                .nomorMeja(nomorMeja)
                .status(status)
                .build();
                
        MejaResponse response2 = MejaResponse.builder()
                .id(id)
                .nomorMeja(nomorMeja)
                .status(status)
                .build();
                
        MejaResponse differentResponse = MejaResponse.builder()
                .id(UUID.randomUUID())
                .nomorMeja("E5")
                .status(MejaStatus.TERISI)
                .build();
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        
        assertNotEquals(response1, differentResponse);
        assertNotEquals(response1.hashCode(), differentResponse.hashCode());
    }
    
    @Test
    void testToString() {
        UUID id = UUID.randomUUID();
        String nomorMeja = "F6";
        MejaStatus status = MejaStatus.TERSEDIA;
        
        MejaResponse response = MejaResponse.builder()
                .id(id)
                .nomorMeja(nomorMeja)
                .status(status)
                .build();
                
        String responseString = response.toString();
        
        assertTrue(responseString.contains(id.toString()));
        assertTrue(responseString.contains(nomorMeja));
        assertTrue(responseString.contains(status.toString()));
    }
}
