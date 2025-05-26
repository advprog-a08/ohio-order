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
    private OrderController orderController;    private UUID mejaId;
    private UUID orderId;
    private OrderDto.OrderRequest orderRequest;
    private UUID orderResponseId;
    private OrderDto.OrderResponse orderResponse;
    private UUID orderItemResponseId;
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

        orderResponseId = UUID.randomUUID();
        orderItemResponseId = UUID.randomUUID();
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
                .total(100000.0)                .createdAt(LocalDateTime.now())
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
        List<CompletableFuture<OrderDto.OrderResponse>> futureResponses =
                List.of(CompletableFuture.completedFuture(orderResponse));

        when(orderService.getOrdersByMejaId(mejaId)).thenReturn(futureResponses);

        ResponseEntity<List<CompletableFuture<OrderDto.OrderResponse>>> response =
                orderController.getOrdersByTableSession(mockTableSession);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());

        OrderDto.OrderResponse actualResponse = response.getBody().get(0).join();
        assertEquals(orderResponse, actualResponse);

        verify(orderService).getOrdersByMejaId(mejaId);
    }    @Test
    void updateOrder_Success() {
        UUID orderId = UUID.randomUUID();
        OrderDto.OrderRequest updateRequest = OrderDto.OrderRequest.builder()
                .items(List.of(
                        OrderDto.OrderItemRequest.builder()
                                .menuItemId("menu-1")
                                .quantity(3)
                                .build()
                ))
                .build();

        when(orderService.updateOrder(eq(orderId), any(OrderDto.OrderRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(orderResponse));

        ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> response =
                orderController.updateOrder(orderId, updateRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        CompletableFuture<OrderDto.OrderResponse> future = response.getBody();
        assertNotNull(future);
        assertEquals(orderResponse, future.join());

        verify(orderService).updateOrder(orderId, updateRequest);
    }

    @Test
    void removeItemFromOrder_Success() {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        when(orderService.removeItemFromOrder(orderId, itemId)).thenReturn(orderResponse);

        ResponseEntity<OrderDto.OrderResponse> response =
                orderController.removeItemFromOrder(orderId, itemId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).removeItemFromOrder(orderId, itemId);
    }

    @Test
    void deleteOrder_Success() {
        UUID orderId = UUID.randomUUID();
        doNothing().when(orderService).deleteOrder(orderId);

        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService).deleteOrder(orderId);
    }
}