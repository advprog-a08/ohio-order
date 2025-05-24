package id.ac.ui.cs.advprog.ohioorder.meja.factory;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MejaResponseFactoryTest {

    private MejaResponseFactory factory;

    @BeforeEach
    void setUp() {
        factory = new MejaResponseFactory();
    }

    @Test
    void testCreateFromEntityWithTersediaStatus() {
        UUID id = UUID.randomUUID();
        Meja meja = Meja.builder()
                .id(id)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();

        MejaResponse response = factory.createFromEntity(meja);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("A1", response.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA, response.getStatus());
    }

    @Test
    void testCreateFromEntityWithTerisiStatus() {
        UUID id = UUID.randomUUID();
        Meja meja = Meja.builder()
                .id(id)
                .nomorMeja("B2")
                .status(MejaStatus.TERISI)
                .build();

        MejaResponse response = factory.createFromEntity(meja);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals("B2", response.getNomorMeja());
        assertEquals(MejaStatus.TERISI, response.getStatus());
    }

    @Test
    void testCreateFromEntityMapsAllFields() {
        UUID id = UUID.randomUUID();
        Meja meja = Meja.builder()
                .id(id)
                .nomorMeja("C3")
                .status(MejaStatus.TERSEDIA)
                .build();

        MejaResponse response = factory.createFromEntity(meja);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertNotNull(response.getNomorMeja());
        assertNotNull(response.getStatus());
        assertEquals(id, response.getId());
        assertEquals("C3", response.getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA, response.getStatus());
    }

    @Test
    void testCreateErrorResponseWithMessage() {
        String errorMessage = "Meja tidak ditemukan";

        MejaResponse response = factory.createErrorResponse(errorMessage);

        assertNotNull(response);
        assertEquals(errorMessage, response.getNomorMeja());
        assertNull(response.getId());
        assertNull(response.getStatus());
    }

    @Test
    void testCreateErrorResponseWithEmptyMessage() {
        String errorMessage = "";

        MejaResponse response = factory.createErrorResponse(errorMessage);

        assertNotNull(response);
        assertEquals("", response.getNomorMeja());
        assertNull(response.getId());
        assertNull(response.getStatus());
    }

    @Test
    void testCreateErrorResponseWithNullMessage() {
        String errorMessage = null;

        MejaResponse response = factory.createErrorResponse(errorMessage);

        assertNotNull(response);
        assertNull(response.getNomorMeja());
        assertNull(response.getId());
        assertNull(response.getStatus());
    }
}