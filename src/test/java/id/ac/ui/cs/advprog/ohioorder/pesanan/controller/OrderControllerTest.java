package id.ac.ui.cs.advprog.ohioorder.pesanan.controller;

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
    private OrderDto.OrderResponse orderResponse;
    private OrderDto.OrderItemRequest itemRequest;
    private OrderDto.UpdateOrderItemRequest updateItemRequest;

    @BeforeEach
    void setUp() {
        mejaId = UUID.randomUUID();

        orderRequest = OrderDto.OrderRequest.builder()
                .mejaId(mejaId)
                .items(List.of(
                        OrderDto.OrderItemRequest.builder()
                                .menuItemId("menu-1")
                                .menuItemName("Burger")
                                .price(50000.0)
                                .quantity(2)
                                .build()
                ))
                .build();

        orderResponse = OrderDto.OrderResponse.builder()
                .id("order-123")
                .mejaId(mejaId)
                .nomorMeja("A1")
                .items(List.of(
                        OrderDto.OrderItemResponse.builder()
                                .id("item-1")
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
                .menuItemName("Pizza")
                .price(75000.0)
                .quantity(1)
                .build();

        updateItemRequest = OrderDto.UpdateOrderItemRequest.builder()
                .quantity(3)
                .build();
    }

    @Test
    void createOrder_Success() {
        when(orderService.createOrder(any(OrderDto.OrderRequest.class))).thenReturn(orderResponse);

        ResponseEntity<OrderDto.OrderResponse> response = orderController.createOrder(orderRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).createOrder(orderRequest);
    }

    @Test
    void getOrdersByMejaId_Success() {
        String mejaIdStr = mejaId.toString();
        when(orderService.getOrdersByMejaId(mejaId)).thenReturn(List.of(orderResponse));

        ResponseEntity<List<OrderDto.OrderResponse>> response = orderController.getOrdersByMejaId(mejaIdStr);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(orderResponse, response.getBody().getFirst());
        verify(orderService).getOrdersByMejaId(mejaId);
    }

    @Test
    void getOrderById_Success() {
        String orderId = "order-123";
        when(orderService.getOrderById(orderId)).thenReturn(orderResponse);

        ResponseEntity<OrderDto.OrderResponse> response = orderController.getOrderById(orderId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).getOrderById(orderId);
    }

    @Test
    void addItemToOrder_Success() {
        String orderId = "order-123";
        when(orderService.addItemToOrder(eq(orderId), any(OrderDto.OrderItemRequest.class))).thenReturn(orderResponse);

        ResponseEntity<OrderDto.OrderResponse> response = orderController.addItemToOrder(orderId, itemRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).addItemToOrder(orderId, itemRequest);
    }

    @Test
    void updateOrderItem_Success() {
        String orderId = "order-123";
        String itemId = "item-1";
        when(orderService.updateOrderItem(eq(orderId), eq(itemId), any(OrderDto.UpdateOrderItemRequest.class)))
                .thenReturn(orderResponse);

        ResponseEntity<OrderDto.OrderResponse> response = orderController.updateOrderItem(orderId, itemId, updateItemRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).updateOrderItem(orderId, itemId, updateItemRequest);
    }

    @Test
    void removeItemFromOrder_Success() {
        String orderId = "order-123";
        String itemId = "item-1";
        when(orderService.removeItemFromOrder(orderId, itemId)).thenReturn(orderResponse);

        ResponseEntity<OrderDto.OrderResponse> response = orderController.removeItemFromOrder(orderId, itemId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderResponse, response.getBody());
        verify(orderService).removeItemFromOrder(orderId, itemId);
    }

    @Test
    void deleteOrder_Success() {
        String orderId = "order-123";
        doNothing().when(orderService).deleteOrder(orderId);

        ResponseEntity<Void> response = orderController.deleteOrder(orderId);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(orderService).deleteOrder(orderId);
    }
}