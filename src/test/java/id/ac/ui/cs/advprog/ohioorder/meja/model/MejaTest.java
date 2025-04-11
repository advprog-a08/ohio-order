package id.ac.ui.cs.advprog.ohioorder.meja.model;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MejaTest {

    private Meja meja;
    private final UUID id = UUID.randomUUID();
    private final String nomorMeja = "A1";

    @BeforeEach
    void setUp() {
        meja = Meja.builder()
                .id(id)
                .nomorMeja(nomorMeja)
                .status(MejaStatus.TERSEDIA)
                .build();
    }

    @Test
    void testMejaCreation() {
        assertEquals(id, meja.getId());
        assertEquals(nomorMeja, meja.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA, meja.getStatus());
    }

    @Test
    void testSetNomorMeja() {
        String newNomorMeja = "B2";
        meja.setNomorMeja(newNomorMeja);
        assertEquals(newNomorMeja, meja.getNomorMeja());
    }

    @Test
    void testSetStatus() {
        meja.setStatus(MejaStatus.TERISI);
        assertEquals(MejaStatus.TERISI, meja.getStatus());
    }

    @Test
    void testIsAvailable() {
        assertTrue(meja.isAvailable());
        
        meja.setStatus(MejaStatus.TERISI);
        assertFalse(meja.isAvailable());
    }

    @Test
    void testNoArgsConstructor() {
        Meja emptyMeja = new Meja();
        assertNull(emptyMeja.getId());
        assertNull(emptyMeja.getNomorMeja());
        assertNull(emptyMeja.getStatus());
    }

    @Test
    void testEqualsAndHashCode() {
        Meja sameMeja = Meja.builder()
                .id(id)
                .nomorMeja(nomorMeja)
                .status(MejaStatus.TERSEDIA)
                .build();
        
        Meja differentMeja = Meja.builder()
                .id(UUID.randomUUID())
                .nomorMeja("C3")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        assertEquals(meja, sameMeja);
        assertEquals(meja.hashCode(), sameMeja.hashCode());
        
        assertNotEquals(meja, differentMeja);
        assertNotEquals(meja.hashCode(), differentMeja.hashCode());
    }

    @Test
    void testToString() {
        String mejaString = meja.toString();
        
        assertTrue(mejaString.contains(id.toString()));
        assertTrue(mejaString.contains(nomorMeja));
        assertTrue(mejaString.contains(MejaStatus.TERSEDIA.toString()));
    }
}