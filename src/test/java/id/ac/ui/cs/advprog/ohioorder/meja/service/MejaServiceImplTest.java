package id.ac.ui.cs.advprog.ohioorder.meja.service;

import id.ac.ui.cs.advprog.ohioorder.grpc.TableSessionGrpcClient;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaRequest;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.dto.TableSessionResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaAlreadyExistsException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaHasPesananException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaNotFoundException;
import id.ac.ui.cs.advprog.ohioorder.meja.exception.MejaNotAvailableException;
import id.ac.ui.cs.advprog.ohioorder.meja.factory.MejaResponseFactory;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.repository.MejaRepository;
import id.ac.ui.cs.advprog.ohioorder.meja.validation.MejaRequestValidator;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import table_session.TableSessionOuterClass;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

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
    private OrderRepository orderRepository;

    @Mock
    private MejaResponseFactory responseFactory;

    @Mock
    private MejaRequestValidator validator;
    
    @Mock
    private TableSessionGrpcClient tableSessionGrpcClient;

    @InjectMocks
    private MejaServiceImpl mejaService;

    private UUID mejaId;
    private Meja meja;
    private UUID orderId;
    private Order order;
    private MejaRequest mejaRequest;
    private MejaResponse mejaResponse;
    private TableSessionOuterClass.TableSessionResponse grpcResponse;

    @BeforeEach
    void setUp() {
        mejaId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        meja = Meja.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();

        order = Order.builder()
                .id(orderId)
                .locked(false)
                .build();

        mejaRequest = MejaRequest.builder()
                .nomorMeja("A1")
                .build();

        mejaResponse = MejaResponse.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();
                
        TableSessionOuterClass.TableSession tableSession = TableSessionOuterClass.TableSession.newBuilder()
                .setId("session-123")
                .setTableId(mejaId.toString())
                .setIsActive(true)
                .build();
                
        grpcResponse = TableSessionOuterClass.TableSessionResponse.newBuilder()
                .setTableSession(tableSession)
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
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        when(responseFactory.createFromEntity(meja)).thenReturn(mejaResponse);

        MejaResponse result = mejaService.getMejaById(mejaId);

        assertNotNull(result);
        assertEquals(meja.getId(), result.getId());
        assertEquals(meja.getNomorMeja(), result.getNomorMeja());
        assertEquals(meja.getStatus(), result.getStatus());

        verify(mejaRepository).findById(mejaId);
        verify(responseFactory).createFromEntity(meja);
    }

    @Test
    void testGetMejaByIdThrowsExceptionWhenNotExists() {
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.getMejaById(mejaId);
        });

        verify(mejaRepository).findById(mejaId);
        verify(responseFactory, never()).createFromEntity(any());
    }

    @Test
    void testUpdateMejaSuccessWhenNoConflict() {
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();

        Meja updatedMeja = Meja.builder()
                .id(mejaId)
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();

        MejaResponse updatedResponse = MejaResponse.builder()
                .id(mejaId)
                .nomorMeja("A2")
                .status(MejaStatus.TERSEDIA)
                .build();

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        when(mejaRepository.save(any(Meja.class))).thenReturn(updatedMeja);
        when(responseFactory.createFromEntity(updatedMeja)).thenReturn(updatedResponse);

        MejaResponse result = mejaService.updateMeja(mejaId, updateRequest);

        assertNotNull(result);
        assertEquals(updatedMeja.getId(), result.getId());
        assertEquals(updatedMeja.getNomorMeja(), result.getNomorMeja());
        assertEquals(updatedMeja.getStatus(), result.getStatus());

        verify(mejaRepository).findById(mejaId);
        verify(validator).validateForUpdate(updateRequest, "A1");
        verify(mejaRepository).save(any(Meja.class));
        verify(responseFactory).createFromEntity(updatedMeja);
    }

    @Test
    void testUpdateMejaThrowsExceptionWhenMejaNotFound() {
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.updateMeja(mejaId, mejaRequest);
        });

        verify(mejaRepository).findById(mejaId);
        verify(validator, never()).validateForUpdate(any(), anyString());
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testUpdateMejaThrowsExceptionWhenNomorMejaConflict() {
        MejaRequest updateRequest = MejaRequest.builder()
                .nomorMeja("A2")
                .build();

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        doThrow(new MejaAlreadyExistsException("Meja dengan nomor A2 sudah ada"))
                .when(validator).validateForUpdate(eq(updateRequest), eq(meja.getNomorMeja()));

        assertThrows(MejaAlreadyExistsException.class, () -> {
            mejaService.updateMeja(mejaId, updateRequest);
        });

        verify(mejaRepository).findById(mejaId);
        verify(validator).validateForUpdate(updateRequest, meja.getNomorMeja());
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testDeleteMejaSuccessWhenAvailable() {
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        doNothing().when(mejaRepository).delete(meja);

        mejaService.deleteMeja(mejaId);

        verify(mejaRepository).findById(mejaId);
        verify(mejaRepository).delete(meja);
    }

    @Test
    void testDeleteMejaThrowsExceptionWhenMejaNotFound() {
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.deleteMeja(mejaId);
        });

        verify(mejaRepository).findById(mejaId);
        verify(mejaRepository, never()).delete(any(Meja.class));
    }

    @Test
    void testDeleteMejaThrowsExceptionWhenMejaHasPesanan() {
        Meja busyMeja = Meja.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(busyMeja));

        assertThrows(MejaHasPesananException.class, () -> {
            mejaService.deleteMeja(mejaId);
        });

        verify(mejaRepository).findById(mejaId);
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
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        MejaResponse updatedResponse = MejaResponse.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        when(mejaRepository.save(any(Meja.class))).thenReturn(updatedMeja);
        when(responseFactory.createFromEntity(updatedMeja)).thenReturn(updatedResponse);

        MejaResponse result = mejaService.setMejaStatus(mejaId, MejaStatus.TERISI);

        assertNotNull(result);
        assertEquals(MejaStatus.TERISI, result.getStatus());

        verify(mejaRepository).findById(mejaId);
        verify(mejaRepository).save(any(Meja.class));
        verify(responseFactory).createFromEntity(updatedMeja);
    }

    @Test
    void testSetMejaStatusThrowsExceptionWhenNotExists() {
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.empty());

        assertThrows(MejaNotFoundException.class, () -> {
            mejaService.setMejaStatus(mejaId, MejaStatus.TERISI);
        });

        verify(mejaRepository).findById(mejaId);
        verify(mejaRepository, never()).save(any(Meja.class));
        verify(responseFactory, never()).createFromEntity(any());
    }

    @Test
    void testIsMejaAvailableReturnsTrueWhenAvailable() {
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));

        boolean result = mejaService.isMejaAvailable(mejaId);

        assertTrue(result);
        verify(mejaRepository).findById(mejaId);
    }

    @Test
    void testIsMejaAvailableReturnsFalseWhenOccupied() {
        Meja busyMeja = Meja.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(busyMeja));

        boolean result = mejaService.isMejaAvailable(mejaId);

        assertFalse(result);
        verify(mejaRepository).findById(mejaId);
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

    @Test
    void testCreateTableSessionSuccessWhenMejaAvailable() {
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(tableSessionGrpcClient.createTableSession(eq(mejaId.toString()), anyString())).thenReturn(grpcResponse);
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);
        
        CompletableFuture<TableSessionResponse> resultFuture = mejaService.createTableSession(mejaId);
 
        TableSessionResponse result = resultFuture.join();
        
        assertNotNull(result);
        assertEquals(mejaId.toString(), result.getTableId());
        assertEquals("session-123", result.getSessionId());
        assertTrue(result.isActive());
        assertEquals("Session created successfully", result.getMessage());
        
        verify(mejaRepository, times(2)).findById(mejaId);
        verify(tableSessionGrpcClient).createTableSession(eq(mejaId.toString()), anyString());
        assertEquals(MejaStatus.TERISI, meja.getStatus());
    }
    
    @Test
    void testCreateTableSessionThrowsExceptionWhenMejaNotFound() {
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.empty());
        
        CompletableFuture<TableSessionResponse> future = mejaService.createTableSession(mejaId);
        
        CompletionException completionException = assertThrows(
            CompletionException.class, 
            () -> future.join()
        );
        
        assertTrue(completionException.getCause() instanceof MejaNotFoundException);
        
        verify(mejaRepository).findById(mejaId);
        verify(tableSessionGrpcClient, never()).createTableSession(anyString(), any(String.class));
        verify(mejaRepository, never()).save(any(Meja.class));
    }
    
    @Test
    void testCreateTableSessionThrowsExceptionWhenMejaNotAvailable() {
        Meja busyMeja = Meja.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();
        
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(busyMeja));
        
        CompletableFuture<TableSessionResponse> future = mejaService.createTableSession(mejaId);
        
        CompletionException completionException = assertThrows(
            CompletionException.class, 
            () -> future.join()
        );
        
        assertTrue(completionException.getCause() instanceof MejaNotAvailableException);
        
        verify(mejaRepository).findById(mejaId);
        verify(tableSessionGrpcClient, never()).createTableSession(anyString(), any(String.class));
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testCreateTableSessionWithEmptyGrpcResponse() {
        TableSessionOuterClass.TableSessionResponse emptyGrpcResponse =
                TableSessionOuterClass.TableSessionResponse.newBuilder().build();

        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(meja));
        when(tableSessionGrpcClient.createTableSession(eq(mejaId.toString()), anyString())).thenReturn(emptyGrpcResponse);
        when(mejaRepository.save(any(Meja.class))).thenReturn(meja);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        CompletableFuture<TableSessionResponse> resultFuture = mejaService.createTableSession(mejaId);
        TableSessionResponse result = resultFuture.join();

        assertNotNull(result);
        assertEquals(mejaId.toString(), result.getTableId());
        assertEquals("", result.getSessionId());
        assertFalse(result.isActive());
        assertEquals("Session created successfully", result.getMessage());
    }

    @Test
    void testCreateTableSessionWhenSecondFindByIdFails() {
        when(mejaRepository.findById(mejaId))
                .thenReturn(Optional.of(meja))
                .thenReturn(Optional.empty());

        CompletableFuture<TableSessionResponse> future = mejaService.createTableSession(mejaId);

        CompletionException completionException = assertThrows(
                CompletionException.class,
                () -> future.join()
        );

        assertTrue(completionException.getCause() instanceof MejaNotFoundException);
        assertTrue(completionException.getCause().getMessage().contains("tidak ditemukan"));

        verify(mejaRepository, times(2)).findById(mejaId);
        verify(tableSessionGrpcClient, never()).createTableSession(anyString(), any(String.class));
    }

    @Test
    void testDeactivateTableSessionSuccess() {
        String sessionId = "session-123";
        
        Meja busyMeja = Meja.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();
        
        TableSessionOuterClass.TableSession deactivatedSession = TableSessionOuterClass.TableSession.newBuilder()
                .setId(sessionId)
                .setTableId(mejaId.toString())
                .setIsActive(false)
                .build();
                
        TableSessionOuterClass.TableSessionResponse deactivationResponse = TableSessionOuterClass.TableSessionResponse.newBuilder()
                .setTableSession(deactivatedSession)
                .build();
        
        when(tableSessionGrpcClient.deactivateTableSession(sessionId)).thenReturn(deactivationResponse);
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.of(busyMeja));
        when(mejaRepository.save(any(Meja.class))).thenReturn(busyMeja);
        
        CompletableFuture<TableSessionResponse> resultFuture = mejaService.deactivateTableSession(sessionId);
        TableSessionResponse result = resultFuture.join();
        
        assertNotNull(result);
        assertEquals(mejaId.toString(), result.getTableId());
        assertEquals(sessionId, result.getSessionId());
        assertFalse(result.isActive());
        assertEquals("Session deactivated successfully", result.getMessage());

        assertEquals(MejaStatus.TERSEDIA, busyMeja.getStatus());
        
        verify(tableSessionGrpcClient).deactivateTableSession(sessionId);
        verify(mejaRepository).findById(mejaId);
        verify(mejaRepository).save(busyMeja);
    }

    @Test
    void testDeactivateTableSessionWithInvalidTableId() {
        String sessionId = "session-123";
        String invalidTableId = "invalid-uuid";
        
        TableSessionOuterClass.TableSession deactivatedSession = TableSessionOuterClass.TableSession.newBuilder()
                .setId(sessionId)
                .setTableId(invalidTableId)
                .setIsActive(false)
                .build();
                
        TableSessionOuterClass.TableSessionResponse deactivationResponse = TableSessionOuterClass.TableSessionResponse.newBuilder()
                .setTableSession(deactivatedSession)
                .build();
        
        when(tableSessionGrpcClient.deactivateTableSession(sessionId)).thenReturn(deactivationResponse);

        CompletableFuture<TableSessionResponse> resultFuture = mejaService.deactivateTableSession(sessionId);
        TableSessionResponse result = resultFuture.join();
        
        assertNotNull(result);
        assertEquals(invalidTableId, result.getTableId());
        assertEquals(sessionId, result.getSessionId());
        assertFalse(result.isActive());
        
        verify(mejaRepository, never()).findById(any(UUID.class));
        verify(mejaRepository, never()).save(any(Meja.class));
    }

    @Test
    void testDeactivateTableSessionWithEmptyGrpcResponse() {
        String sessionId = "session-123";

        TableSessionOuterClass.TableSessionResponse emptyResponse = 
                TableSessionOuterClass.TableSessionResponse.newBuilder().build();
        
        when(tableSessionGrpcClient.deactivateTableSession(sessionId)).thenReturn(emptyResponse);
        
        CompletableFuture<TableSessionResponse> resultFuture = mejaService.deactivateTableSession(sessionId);
        TableSessionResponse result = resultFuture.join();
        
        assertNotNull(result);
        assertEquals("", result.getTableId());
        assertEquals("", result.getSessionId());
        assertFalse(result.isActive());
        assertEquals("Session deactivated successfully", result.getMessage());
        
        verify(mejaRepository, never()).findById(any(UUID.class));
        verify(mejaRepository, never()).save(any(Meja.class));
    }
    
    @Test
    void testDeactivateTableSessionWhenMejaNotFound() {
        String sessionId = "session-123";
        
        TableSessionOuterClass.TableSession deactivatedSession = TableSessionOuterClass.TableSession.newBuilder()
                .setId(sessionId)
                .setTableId(mejaId.toString())
                .setIsActive(false)
                .build();
                
        TableSessionOuterClass.TableSessionResponse deactivationResponse = 
                TableSessionOuterClass.TableSessionResponse.newBuilder()
                .setTableSession(deactivatedSession)
                .build();
        
        when(tableSessionGrpcClient.deactivateTableSession(sessionId)).thenReturn(deactivationResponse);
        when(mejaRepository.findById(mejaId)).thenReturn(Optional.empty());
        
        CompletableFuture<TableSessionResponse> resultFuture = mejaService.deactivateTableSession(sessionId);
        
        CompletionException exception = assertThrows(
                CompletionException.class,
                () -> resultFuture.join()
        );
        
        assertTrue(exception.getCause() instanceof MejaNotFoundException);
        assertTrue(exception.getCause().getMessage().contains("tidak ditemukan"));
        
        verify(tableSessionGrpcClient).deactivateTableSession(sessionId);
        verify(mejaRepository).findById(mejaId);
        verify(mejaRepository, never()).save(any(Meja.class));
    }
    
    @Test
    void testDeactivateTableSessionWithGrpcException() {
        String sessionId = "session-123";
        
        when(tableSessionGrpcClient.deactivateTableSession(sessionId)).thenThrow(
                new RuntimeException("gRPC service unavailable"));
        
        CompletableFuture<TableSessionResponse> resultFuture = mejaService.deactivateTableSession(sessionId);
        
        CompletionException exception = assertThrows(
                CompletionException.class,
                () -> resultFuture.join()
        );
        
        assertTrue(exception.getCause() instanceof RuntimeException);
        assertEquals("gRPC service unavailable", exception.getCause().getMessage());
        
        verify(tableSessionGrpcClient).deactivateTableSession(sessionId);
        verify(mejaRepository, never()).findById(any(UUID.class));
        verify(mejaRepository, never()).save(any(Meja.class));
    }
}