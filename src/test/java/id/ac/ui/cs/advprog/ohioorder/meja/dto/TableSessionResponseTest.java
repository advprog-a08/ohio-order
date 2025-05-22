package id.ac.ui.cs.advprog.ohioorder.meja.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TableSessionResponseTest {

    @Test
    void testTableSessionResponseBuilder() {
        String tableId = UUID.randomUUID().toString();
        String sessionId = "session-123";
        boolean isActive = true;
        String message = "Test message";
        
        TableSessionResponse response = TableSessionResponse.builder()
                .tableId(tableId)
                .sessionId(sessionId)
                .isActive(isActive)
                .message(message)
                .build();
        
        assertEquals(tableId, response.getTableId());
        assertEquals(sessionId, response.getSessionId());
        assertEquals(isActive, response.isActive());
        assertEquals(message, response.getMessage());
    }
    
    @Test
    void testNoArgsConstructor() {
        TableSessionResponse response = new TableSessionResponse();
        
        assertNull(response.getTableId());
        assertNull(response.getSessionId());
        assertFalse(response.isActive());
        assertNull(response.getMessage());
    }
    
    @Test
    void testAllArgsConstructor() {
        String tableId = UUID.randomUUID().toString();
        String sessionId = "session-456";
        boolean isActive = false;
        String message = "Another test message";
        
        TableSessionResponse response = new TableSessionResponse(tableId, sessionId, isActive, message);
        
        assertEquals(tableId, response.getTableId());
        assertEquals(sessionId, response.getSessionId());
        assertEquals(isActive, response.isActive());
        assertEquals(message, response.getMessage());
    }
    
    @Test
    void testSettersAndGetters() {
        TableSessionResponse response = new TableSessionResponse();
        
        String tableId = UUID.randomUUID().toString();
        String sessionId = "session-789";
        boolean isActive = true;
        String message = "Third test message";
        
        response.setTableId(tableId);
        response.setSessionId(sessionId);
        response.setActive(isActive);
        response.setMessage(message);
        
        assertEquals(tableId, response.getTableId());
        assertEquals(sessionId, response.getSessionId());
        assertEquals(isActive, response.isActive());
        assertEquals(message, response.getMessage());
    }
    
    @Test
    void testEqualsAndHashCode() {
        String tableId = UUID.randomUUID().toString();
        String sessionId = "session-123";
        
        TableSessionResponse response1 = TableSessionResponse.builder()
                .tableId(tableId)
                .sessionId(sessionId)
                .isActive(true)
                .message("Test message")
                .build();
                
        TableSessionResponse response2 = TableSessionResponse.builder()
                .tableId(tableId)
                .sessionId(sessionId)
                .isActive(true)
                .message("Test message")
                .build();
                
        TableSessionResponse differentResponse = TableSessionResponse.builder()
                .tableId(UUID.randomUUID().toString())
                .sessionId("different-session")
                .isActive(false)
                .message("Different message")
                .build();
        
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        
        assertNotEquals(response1, differentResponse);
        assertNotEquals(response1.hashCode(), differentResponse.hashCode());
    }
    
    @Test
    void testToString() {
        String tableId = UUID.randomUUID().toString();
        String sessionId = "session-123";
        
        TableSessionResponse response = TableSessionResponse.builder()
                .tableId(tableId)
                .sessionId(sessionId)
                .isActive(true)
                .message("Test message")
                .build();
        
        String responseString = response.toString();
        
        assertTrue(responseString.contains(tableId));
        assertTrue(responseString.contains(sessionId));
        assertTrue(responseString.contains("true"));
        assertTrue(responseString.contains("Test message"));
    }
}
