package id.ac.ui.cs.advprog.ohioorder.meja.service;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaAlreadyExistsException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaHasPesananException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaNotFoundException;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.repository.MejaRepository;
import id.ac.ui.cs.advprog.ohioorder.meja.service.impl.MejaServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MejaServiceImplTest {

    @Mock
    private MejaRepository mejaRepository;

    @InjectMocks
    private MejaServiceImpl mejaService;

    private UUID uuid;
    private Meja meja;
    private MejaRequest mejaRequest;

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
    }

    @Test
    void createMeja_Success() {
        // Arrange
        when(mejaRepository.existsByNomorMeja(mejaRequest.getNomorMeja())).thenReturn(false);
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);

        // Act
        MejaResponse result = mejaService.createMeja(mejaRequest);

        // Assert
        assertNotNull(result);
        assertEquals(meja.getId(), result.getId());
        assertEquals(meja.getNomorMeja(), result.getNomorMeja());
        assertEquals(meja.getStatus(), result.getStatus());
        
        verify(mejaRepository).existsByNomorMeja(mejaRequest.getNomorMeja());
        verify(mejaRepository).save(any(Meja.class));
    }

    @Test
    void createMeja_ThrowsException_WhenMejaExists() {
        // Arrange
        when(mejaRepository.existsByNomorMeja(mejaRequest.getNomorMeja())).thenReturn(true);

        // Act & Assert
        assertThrows(MejaAlreadyExistsException.class, () -> {
            mejaService.createMeja(mejaRequest);
        });
        
        verify(mejaRepository).existsByNomorMeja(mejaRequest.getNomorMeja());
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void getAllMeja_ReturnsAllMeja() {
        // Arrange
        Meja meja2 = Meja.builder()
                .id(UUID.randomUUID())
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        when(mejaRepository.findAll()).thenReturn(Arrays.asList(meja, meja2));

        // Act
        List<MejaResponse> result = mejaService.getAllMeja();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(meja.getId(), result.get(0).getId());
        assertEquals(meja.getNomorMeja(), result.get(0).getNomorMeja());
        assertEquals(meja.getStatus(), result.get(0).getStatus());
        assertEquals(meja2.getId(), result.get(1).getId());
        
        verify(mejaRepository).findAll();
    }

    @Test
    void getMejaById_ReturnsMeja_WhenExists() {
        // Arrange
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));

        // Act
        MejaResponse result = mejaService.getMejaById(uuid);

        // Assert
        assertNotNull(result);
        assertEquals(meja.getId(), result.getId());
        assertEquals(meja.getNomorMeja(), result.getNomorMeja());
        assertEquals(meja.getStatus(), result.getStatus());
        
        verify(mejaRepository).findById(uuid);
    }

    @Test
    void getMejaById_ThrowsException_WhenNotExists() {
        // Arrange
        when(mejaRepository.findById(uuid)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaById(uuid);
        });
        
        verify(mejaRepository).findById(uuid);
    }

    @Test
    void updateMeja_Success_WhenNoConflict() {
        // Arrange
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();
        
        Meja updatedMeja = Meja.builder()
                .id(uuid)
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        when(mejaRepository.findByNomorMeja(updateRequest.getNomorMeja())).thenReturn(Optional.empty());
        when(mejaRepository.save(any(Meja.class))).thenReturn(updatedMeja);

        // Act
        MejaResponse result = mejaService.updateMeja(uuid, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(updatedMeja.getId(), result.getId());
        assertEquals(updatedMeja.getNomorMeja(), result.getNomorMeja());
        assertEquals(updatedMeja.getStatus(), result.getStatus());
        
        verify(mejaRepository).findById(uuid);
        verify(mejaRepository).findByNomorMeja(updateRequest.getNomorMeja());
        verify(mejaRepository).save(any(Meja.class));
    }

    @Test
    void updateMeja_ThrowsException_WhenMejaNotFound() {
        // Arrange
        when(mejaRepository.findById(uuid)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.updateMeja(uuid, mejaRequest);
        });
        
        verify(mejaRepository).findById(uuid);
        verify(mejaRepository, never()).findByNomorMeja(any());
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void updateMeja_ThrowsException_WhenNomorMejaConflict() {
        // Arrange
        UUID otherUuid = UUID.randomUUID();
        Meja otherMeja = Meja.builder()
                .id(otherUuid)
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();
        
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();
        
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        when(mejaRepository.findByNomorMeja(updateRequest.getNomorMeja())).thenReturn(Optional.of(otherMeja));

        // Act & Assert
        assertThrows(MejaAlreadyExistsException.class, () -> {
            mejaService.updateMeja(uuid, updateRequest);
        });
        
        verify(mejaRepository).findById(uuid);
        verify(mejaRepository).findByNomorMeja(updateRequest.getNomorMeja());
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void deleteMeja_Success_WhenAvailable() {
        // Arrange
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        doNothing().when(mejaRepository).delete(meja);

        // Act
        mejaService.deleteMeja(uuid);

        // Assert
        verify(mejaRepository).findById(uuid);
        verify(mejaRepository).delete(meja);
    }

    @Test
    void deleteMeja_ThrowsException_WhenMejaNotFound() {
        // Arrange
        when(mejaRepository.findById(uuid)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.deleteMeja(uuid);
        });
        
        verify(mejaRepository).findById(uuid);
        verify(mejaRepository, never()).delete(any(Meja.class));
    }

    @Test
    void deleteMeja_ThrowsException_WhenMejaHasPesanan() {
        // Arrange
        Meja busyMeja = Meja.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();
        
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(busyMeja));

        // Act & Assert
        assertThrows(MejaHasPesananException.class, () -> {
            mejaService.deleteMeja(uuid);
        });
        
        verify(mejaRepository).findById(uuid);
        verify(mejaRepository, never()).delete(any(Meja.class));
    }

    @Test
    void getMejaByNomorMeja_ReturnsMeja_WhenExists() {
        // Arrange
        when(mejaRepository.findByNomorMeja("A1")).thenReturn(Optional.of(meja));

        // Act
        MejaResponse result = mejaService.getMejaByNomorMeja("A1");

        // Assert
        assertNotNull(result);
        assertEquals(meja.getId(), result.getId());
        assertEquals(meja.getNomorMeja(), result.getNomorMeja());
        
        verify(mejaRepository).findByNomorMeja("A1");
    }

    @Test
    void getMejaByNomorMeja_ThrowsException_WhenNotExists() {
        // Arrange
        when(mejaRepository.findByNomorMeja("A1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaByNomorMeja("A1");
        });
        
        verify(mejaRepository).findByNomorMeja("A1");
    }

    @Test
    void setMejaStatus_Updates_WhenExists() {
        // Arrange
        Meja updatedMeja = Meja.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();
        
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));
        when(mejaRepository.save(any(Meja.class))).thenReturn(updatedMeja);

        // Act
        MejaResponse result = mejaService.setMejaStatus(uuid, MejaStatus.TERISI);

        // Assert
        assertNotNull(result);
        assertEquals(MejaStatus.TERISI, result.getStatus());
        
        verify(mejaRepository).findById(uuid);
        verify(mejaRepository).save(any(Meja.class));
    }

    @Test
    void setMejaStatus_ThrowsException_WhenNotExists() {
        // Arrange
        when(mejaRepository.findById(uuid)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.setMejaStatus(uuid, MejaStatus.TERISI);
        });
        
        verify(mejaRepository).findById(uuid);
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void isMejaAvailable_ReturnsTrue_WhenAvailable() {
        // Arrange
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(meja));

        // Act
        boolean result = mejaService.isMejaAvailable(uuid);

        // Assert
        assertTrue(result);
        verify(mejaRepository).findById(uuid);
    }

    @Test
    void isMejaAvailable_ReturnsFalse_WhenOccupied() {
        // Arrange
        Meja busyMeja = Meja.builder()
                .id(uuid)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();
        
        when(mejaRepository.findById(uuid)).thenReturn(Optional.of(busyMeja));

        // Act
        boolean result = mejaService.isMejaAvailable(uuid);

        // Assert
        assertFalse(result);
        verify(mejaRepository).findById(uuid);
    }

    @Test
    void getAvailableMeja_ReturnsOnlyAvailableMeja() {
        // Arrange
        Meja meja2 = Meja.builder()
                .id(UUID.randomUUID())
                .nomorMeja("A2")
                .status(MejaStatus.TERISI)
                .build();
        
        when(mejaRepository.findAll()).thenReturn(Arrays.asList(meja, meja2));

        // Act
        List<MejaResponse> result = mejaService.getAvailableMeja();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(meja.getId(), result.get(0).getId());
        assertEquals(MejaStatus.TERSEDIA, result.get(0).getStatus());
        
        verify(mejaRepository).findAll();
    }
}