package id.ac.ui.cs.advprog.ohioorder.meja.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejaRequestTest {

    @Test
    void testMejaRequestBuilder() {
        String nomorMeja = "A1";
        
        MejaRequest request = MejaRequest.builder()
                .nomorMeja(nomorMeja)
                .build();
        
        assertEquals(nomorMeja, request.getNomorMeja());
    }
    
    @Test
    void testNoArgsConstructor() {
        MejaRequest request = new MejaRequest();
        
        assertNull(request.getNomorMeja());
    }
    
    @Test
    void testAllArgsConstructor() {
        String nomorMeja = "B2";
        
        MejaRequest request = new MejaRequest(nomorMeja);
        
        assertEquals(nomorMeja, request.getNomorMeja());
    }
    
    @Test
    void testSettersAndGetters() {
        MejaRequest request = new MejaRequest();
        String nomorMeja = "C3";
        
        request.setNomorMeja(nomorMeja);
        
        assertEquals(nomorMeja, request.getNomorMeja());
    }
    
    @Test
    void testEqualsAndHashCode() {
        String nomorMeja = "D4";
        
        MejaRequest request1 = MejaRequest.builder().nomorMeja(nomorMeja).build();
        MejaRequest request2 = MejaRequest.builder().nomorMeja(nomorMeja).build();
        MejaRequest differentRequest = MejaRequest.builder().nomorMeja("E5").build();
        
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        
        assertNotEquals(request1, differentRequest);
        assertNotEquals(request1.hashCode(), differentRequest.hashCode());
    }
    
    @Test
    void testToString() {
        String nomorMeja = "F6";
        
        MejaRequest request = MejaRequest.builder().nomorMeja(nomorMeja).build();
        String requestString = request.toString();
        
        assertTrue(requestString.contains(nomorMeja));
    }
}
