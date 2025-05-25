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
    private OrderDto.OrderRequest orderRequest;
    private UUID orderResponseId;
    private OrderDto.OrderResponse orderResponse;
    private UUID orderItemResponseId;
    private OrderDto.OrderItemRequest itemRequest;
    private OrderDto.UpdateOrderItemRequest updateItemRequest;
    private TableSession mockTableSession;

    @BeforeEach
    void setUp() {
        mejaId = UUID.randomUUID();

        mockTableSession = new TableSession("session-123", mejaId.toString(), true);

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
                .total(100000.0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId("menu-2")
                .quantity(1)
                .build();

        updateItemRequest = OrderDto.UpdateOrderItemRequest.builder()
                .quantity(3)
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

        // Extract and verify the future result
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

        // Extract and verify the future result
        OrderDto.OrderResponse actualResponse = response.getBody().get(0).join();
        assertEquals(orderResponse, actualResponse);

        verify(orderService).getOrdersByMejaId(mejaId);
    }

    @Test
    void getOrderById_Success() {
        UUID orderId = UUID.randomUUID();

        when(orderService.getOrderById(orderId)).thenReturn(CompletableFuture.completedFuture(orderResponse));

        ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> response =
                orderController.getOrderById(orderId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Extract and verify the future result
        CompletableFuture<OrderDto.OrderResponse> future = response.getBody();
        assertNotNull(future);
        assertEquals(orderResponse, future.join());

        verify(orderService).getOrderById(orderId);
    }

    @Test
    void addItemToOrder_Success() {
        UUID orderId = UUID.randomUUID();

        when(orderService.addItemToOrder(eq(orderId), any(OrderDto.OrderItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(orderResponse));

        ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> response =
                orderController.addItemToOrder(orderId, itemRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Extract and verify the future result
        CompletableFuture<OrderDto.OrderResponse> future = response.getBody();
        assertNotNull(future);
        assertEquals(orderResponse, future.join());

        verify(orderService).addItemToOrder(orderId, itemRequest);
    }

    @Test
    void updateOrderItem_Success() {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        when(orderService.updateOrderItem(eq(orderId), eq(itemId), any(OrderDto.UpdateOrderItemRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(orderResponse));

        ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> response =
                orderController.updateOrderItem(orderId, itemId, updateItemRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Extract and verify the future result
        CompletableFuture<OrderDto.OrderResponse> future = response.getBody();
        assertNotNull(future);
        assertEquals(orderResponse, future.join());

        verify(orderService).updateOrderItem(orderId, itemId, updateItemRequest);
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