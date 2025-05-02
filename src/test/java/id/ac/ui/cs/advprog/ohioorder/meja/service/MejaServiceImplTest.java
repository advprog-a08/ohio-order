package id.ac.ui.cs.advprog.ohioorder.meja.service;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaAlreadyExistsException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaHasPesananException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaNotFoundException;
import id.ac.ui.cs.advprog.ohioorder.meja.factory.MejaResponseFactory;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.repository.MejaRepository;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaServiceImpl;
import id.ac.ui.cs.advprog.ohioorder.meja.validation.MejaRequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MejaServiceImplTest {

    @Mock
    private MejaRepository mejaRepository;

    @Mock
    private MejaResponseFactory responseFactory;

    @Mock
    private MejaRequestValidator validator;

    @InjectMocks
    private MejaServiceImpl mejaService;

    private UUID uuid;
    private Meja meja;
    private MejaRequest mejaRequest;
    private MejaResponse mejaResponse;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID();

        meja = Meja.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();

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
    void testCreateMejaSuccess() {
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);
        when(responseFactory.createFromEntity(any(Meja.class))).thenReturn(mejaResponse);

        MejaResponse result = mejaService.createMeja(mejaRequest);

        assertNotNull(result);
        assertEquals(meja.getId(), result.getId());
        assertEquals(meja.getNomorMeja(), result.getNomorMeja());
        assertEquals(meja.getStatus(), result.getStatus());

        verify(validator).validate(mejaRequest);
        verify(mejaRepository).save(any(Meja.class));
        verify(responseFactory).createFromEntity(any(Meja.class));
    }

    @Test
    void testCreateMejaThrowsExceptionWhenMejaExists() {
        doThrow(new MejaAlreadyExistsException("Meja dengan nomor A1 sudah ada"))
                .when(validator).validate(mejaRequest);

        assertThrows(MejaAlreadyExistsException.class, () -> {
            mejaService.createMeja(mejaRequest);
        });

        verify(validator).validate(mejaRequest);
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testGetAllMejaReturnsAllMeja() {
        Meja meja2 = Meja.builder()
                .id(UUID.randomUUID())
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();

        MejaResponse mejaResponse2 = MejaResponse.builder()
                .id(meja2.getId())
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();

        when(mejaRepository.findAll()).thenReturn(Arrays.asList(meja, meja2));
        when(responseFactory.createFromEntity(meja)).thenReturn(mejaResponse);
        when(responseFactory.createFromEntity(meja2)).thenReturn(mejaResponse2);

        List<MejaResponse> result = mejaService.getAllMeja();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(meja.getId(), result.get(0).getId());
        assertEquals(meja.getNomorMeja(), result.get(0).getNomorMeja());
        assertEquals(meja.getStatus(), result.get(0).getStatus());
        assertEquals(meja2.getId(), result.get(1).getId());

        verify(mejaRepository).findAll();
        verify(responseFactory, times(2)).createFromEntity(any(Meja.class));
    }

    @Test
    void testGetMejaByIdReturnsMejaWhenExists() {
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        when(responseFactory.createFromEntity(meja)).thenReturn(mejaResponse);

        MejaResponse result = mejaService.getMejaById(uuid);

        assertNotNull(result);
        assertEquals(meja.getId(), result.getId());
        assertEquals(meja.getNomorMeja(), result.getNomorMeja());
        assertEquals(meja.getStatus(), result.getStatus());

        verify(mejaRepository).findById(uuid);
        verify(responseFactory).createFromEntity(meja);
    }

    @Test
    void testGetMejaByIdThrowsExceptionWhenNotExists() {
        when(mejaRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaById(uuid);
        });

        verify(mejaRepository).findById(uuid);
        verify(responseFactory, never()).createFromEntity(any());
    }

    @Test
    void testUpdateMejaSuccessWhenNoConflict() {
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();

        Meja updatedMeja = Meja.builder()
                .id(uuid)
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();

        MejaResponse updatedResponse = MejaResponse.builder()
                .id(uuid)
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();

        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        when(mejaRepository.save(any(Meja.class))).thenReturn(updatedMeja);
        when(responseFactory.createFromEntity(updatedMeja)).thenReturn(updatedResponse);

        MejaResponse result = mejaService.updateMeja(uuid, updateRequest);

        assertNotNull(result);
        assertEquals(updatedMeja.getId(), result.getId());
        assertEquals(updatedMeja.getNomorMeja(), result.getNomorMeja());
        assertEquals(updatedMeja.getStatus(), result.getStatus());

        verify(mejaRepository).findById(uuid);
        verify(validator).validateForUpdate(updateRequest, "A1");
        verify(mejaRepository).save(any(Meja.class));
        verify(responseFactory).createFromEntity(updatedMeja);
    }

    @Test
    void testUpdateMejaThrowsExceptionWhenMejaNotFound() {
        when(mejaRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.updateMeja(uuid, mejaRequest);
        });

        verify(mejaRepository).findById(uuid);
        verify(validator, never()).validateForUpdate(any(), anyString());
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testUpdateMejaThrowsExceptionWhenNomorMejaConflict() {
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();

        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        doThrow(new MejaAlreadyExistsException("Meja dengan nomor A2 sudah ada"))
                .when(validator).validateForUpdate(eq(updateRequest), eq(meja.getNomorMeja()));

        assertThrows(MejaAlreadyExistsException.class, () -> {
            mejaService.updateMeja(uuid, updateRequest);
        });

        verify(mejaRepository).findById(uuid);
        verify(validator).validateForUpdate(updateRequest, meja.getNomorMeja());
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testDeleteMejaSuccessWhenAvailable() {
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        doNothing().when(mejaRepository).delete(meja);

        mejaService.deleteMeja(uuid);

        verify(mejaRepository).findById(uuid);
        verify(mejaRepository).delete(meja);
    }

    @Test
    void testDeleteMejaThrowsExceptionWhenMejaNotFound() {
        when(mejaRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.deleteMeja(uuid);
        });

        verify(mejaRepository).findById(uuid);
        verify(mejaRepository, never()).delete(any(Meja.class));
    }

    @Test
    void testDeleteMejaThrowsExceptionWhenMejaHasPesanan() {
        Meja busyMeja = Meja.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(busyMeja));

        assertThrows(MejaHasPesananException.class, () -> {
            mejaService.deleteMeja(uuid);
        });

        verify(mejaRepository).findById(uuid);
        verify(mejaRepository, never()).delete(any(Meja.class));
    }

    @Test
    void testGetMejaByNomorMejaReturnsMejaWhenExists() {
        when(mejaRepository.findByNomorMeja("A1")).thenReturn(Optional.of(meja));
        when(responseFactory.createFromEntity(meja)).thenReturn(mejaResponse);

        MejaResponse result = mejaService.getMejaByNomorMeja("A1");

        assertNotNull(result);
        assertEquals(meja.getId(), result.getId());
        assertEquals(meja.getNomorMeja(), result.getNomorMeja());

        verify(mejaRepository).findByNomorMeja("A1");
        verify(responseFactory).createFromEntity(meja);
    }

    @Test
    void testGetMejaByNomorMejaThrowsExceptionWhenNotExists() {
        when(mejaRepository.findByNomorMeja("A1")).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaByNomorMeja("A1");
        });

        verify(mejaRepository).findByNomorMeja("A1");
        verify(responseFactory, never()).createFromEntity(any());
    }

    @Test
    void testSetMejaStatusUpdatesWhenExists() {
        Meja updatedMeja = Meja.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        MejaResponse updatedResponse = MejaResponse.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        when(mejaRepository.save(any(Meja.class))).thenReturn(updatedMeja);
        when(responseFactory.createFromEntity(updatedMeja)).thenReturn(updatedResponse);

        MejaResponse result = mejaService.setMejaStatus(uuid, MejaStatus.TERISI);

        assertNotNull(result);
        assertEquals(MejaStatus.TERISI, result.getStatus());

        verify(mejaRepository).findById(uuid);
        verify(mejaRepository).save(any(Meja.class));
        verify(responseFactory).createFromEntity(updatedMeja);
    }

    @Test
    void testSetMejaStatusThrowsExceptionWhenNotExists() {
        when(mejaRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.setMejaStatus(uuid, MejaStatus.TERISI);
        });

        verify(mejaRepository).findById(uuid);
        verify(mejaRepository, never()).save(any(Meja.class));
        verify(responseFactory, never()).createFromEntity(any());
    }

    @Test
    void testIsMejaAvailableReturnsTrueWhenAvailable() {
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));

        boolean result = mejaService.isMejaAvailable(uuid);

        assertTrue(result);
        verify(mejaRepository).findById(uuid);
    }

    @Test
    void testIsMejaAvailableReturnsFalseWhenOccupied() {
        Meja busyMeja = Meja.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(busyMeja));

        boolean result = mejaService.isMejaAvailable(uuid);

        assertFalse(result);
        verify(mejaRepository).findById(uuid);
    }

    @Test
    void testGetAvailableMejaReturnsOnlyAvailableMeja() {
        Meja meja2 = Meja.builder()
                .id(UUID.randomUUID())
                .nomorMeja("A2")
                .status(MejaStatus.TERISI)
                .build();

        when(mejaRepository.findAll()).thenReturn(Arrays.asList(meja, meja2));
        when(responseFactory.createFromEntity(meja)).thenReturn(mejaResponse);

        List<MejaResponse> result = mejaService.getAvailableMeja();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(meja.getId(), result.get(0).getId());
        assertEquals(MejaStatus.TERSEDIA, result.get(0).getStatus());

        verify(mejaRepository).findAll();
        verify(responseFactory).createFromEntity(meja);
        verify(responseFactory, never()).createFromEntity(meja2);
    }
}