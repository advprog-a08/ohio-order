package id.ac.ui.cs.advprog.ohioorder.pesanan.controller;

import id.ac.ui.cs.advprog.ohioorder.model.TableSession;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderServiceImpl orderService;

    @InjectMocks
    private OrderController orderController;

    private UUID mejaId;
    private UUID orderId;
    private OrderDto.OrderRequest orderRequest;
    private OrderDto.OrderResponse orderResponse;
    private TableSession mockTableSession;

    @BeforeEach
    void setUp() {
        mejaId = UUID.randomUUID();
        orderId = UUID.randomUUID();

        mockTableSession = new TableSession("session-123", mejaId.toString(), orderId.toString(), Optional.empty(), true);

        orderRequest = OrderDto.OrderRequest.builder()
                .items(List.of(
                        OrderDto.OrderItemRequest.builder()
                                .menuItemId("menu-1")
                                .quantity(2)
                                .build()
                ))
                .build();

        UUID orderResponseId = UUID.randomUUID();
        UUID orderItemResponseId = UUID.randomUUID();
        orderResponse = OrderDto.OrderResponse.builder()
                .id(orderResponseId)
                .mejaId(mejaId)
                .nomorMeja("A1")
                .items(List.of(
                        OrderDto.OrderItemResponse.builder()
                                .id(orderItemResponseId)
                                .menuItemId("menu-1")
                                .menuItemName("Burger")
                                .price(50000.0)
                                .quantity(2)
                                .subtotal(100000.0)
                                .build()
                ))
                .total(100000.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createOrder_Success() {
        when(orderService.createOrder(any(OrderDto.OrderRequest.class), eq(mejaId)))
                .thenReturn(CompletableFuture.completedFuture(orderResponse));

        ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> response =
                orderController.createOrder(mockTableSession, orderRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        CompletableFuture<OrderDto.OrderResponse> future = response.getBody();
        assertNotNull(future);
        assertEquals(orderResponse, future.join());

        verify(orderService).createOrder(orderRequest, mejaId);
    }

    @Test
    void getOrdersByTableSession_Success() {
        when(orderService.getOrderById(orderId))
                .thenReturn(CompletableFuture.completedFuture(orderResponse));

        ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> response =
                orderController.getOrdersByTableSession(mockTableSession);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CompletableFuture<OrderDto.OrderResponse> future = response.getBody();
        assertNotNull(future);
        assertEquals(orderResponse, future.join());

        verify(orderService).getOrderById(orderId);
    }

    @Test
    void updateOrder_Success() {
        OrderDto.OrderRequest updateRequest = OrderDto.OrderRequest.builder()
                .items(List.of(
                        OrderDto.OrderItemRequest.builder()
                                .menuItemId("menu-1")
                                .quantity(3)
                                .build()
                ))
                .build();

        when(orderService.updateOrder(eq(String.valueOf(orderId)), any(OrderDto.OrderRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(orderResponse));

        ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> response =
                orderController.updateOrder(mockTableSession, updateRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CompletableFuture<OrderDto.OrderResponse> future = response.getBody();
        assertNotNull(future);
        assertEquals(orderResponse, future.join());

        verify(orderService).updateOrder(String.valueOf(orderId), updateRequest);
    }

    @Test
    void removeItemFromOrder_Success() {
        UUID itemId = UUID.randomUUID();

        when(orderService.removeItemFromOrder(String.valueOf(orderId), itemId)).thenReturn(orderResponse);

        ResponseEntity<OrderDto.OrderResponse> response =
                orderController.removeItemFromOrder(mockTableSession, itemId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponse, response.getBody());

        verify(orderService).removeItemFromOrder(String.valueOf(orderId), itemId);
    }

    @Test
    void deleteOrder_Success() {
        UUID orderIdToDelete = UUID.randomUUID();
        doNothing().when(orderService).deleteOrder(orderIdToDelete);

        ResponseEntity<Void> response = orderController.deleteOrder(orderIdToDelete);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService).deleteOrder(orderIdToDelete);
    }
}