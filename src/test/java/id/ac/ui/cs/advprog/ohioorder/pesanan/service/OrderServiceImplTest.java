package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderMapper;
import id.ac.ui.cs.advprog.ohioorder.pesanan.enums.OrderStatus;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private MejaService mejaService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID mejaId;
    private Order order;
    private OrderDto.OrderRequest orderRequest;
    private OrderDto.OrderResponse orderResponse;
    private Meja meja;
    private MejaResponse mejaResponse;

    @BeforeEach
    void setUp() {
        mejaId = UUID.randomUUID();

        meja = Meja.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();

        mejaResponse = MejaResponse.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();

        order = Order.builder()
                .id("order-123")
                .userId("user-123")
                .meja(meja)
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        orderRequest = OrderDto.OrderRequest.builder()
                .mejaId(mejaId)
                .userId("user-123")
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
                .userId("user-123")
                .status(OrderStatus.PENDING)
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
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    @Test
    void createOrder_Success() {
        when(mejaService.getMejaById(mejaId)).thenReturn(mejaResponse);
        when(orderRepository.findByMejaIdAndStatus(mejaId, OrderStatus.PENDING)).thenReturn(Collections.emptyList());
        when(orderMapper.toEntity(orderRequest)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        OrderDto.OrderResponse result = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(result);
        assertEquals("order-123", result.getId());
        assertEquals(mejaId, result.getMejaId());
        assertEquals("user-123", result.getUserId());
        verify(mejaService).setMejaStatus(mejaId, MejaStatus.TERISI);
        verify(orderRepository).save(order);
    }

    @Test
    void createOrder_ThrowsException_WhenTableNotAvailable() {
        MejaResponse mejaResponseFail = MejaResponse.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        when(mejaService.getMejaById(mejaId)).thenReturn(mejaResponseFail);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(orderRequest));
        assertEquals("Table is not available for ordering", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ThrowsException_WhenTableHasPendingOrder() {
        when(mejaService.getMejaById(mejaId)).thenReturn(mejaResponse);
        when(orderRepository.findByMejaIdAndStatus(mejaId, OrderStatus.PENDING)).thenReturn(List.of(order));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(orderRequest));
        assertEquals("This table already has a pending order", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrdersByUserId_Success() {
        // Arrange
        String userId = "user-123";
        when(orderRepository.findByUserId(userId)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        List<OrderDto.OrderResponse> result = orderService.getOrdersByUserId(userId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("order-123", result.get(0).getId());
    }

    @Test
    void getOrdersByMejaId_Success() {
        // Arrange
        when(orderRepository.findByMejaId(mejaId)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        List<OrderDto.OrderResponse> result = orderService.getOrdersByMejaId(mejaId);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("order-123", result.get(0).getId());
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        String orderId = "order-123";
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        OrderDto.OrderResponse result = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void getOrderById_ThrowsException_WhenOrderNotFound() {
        // Arrange
        String orderId = "nonexistent-order";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.getOrderById(orderId));
        assertEquals("Order not found with ID: " + orderId, exception.getMessage());
    }

    @Test
    void addItemToOrder_Success_WithNewItem() {
        // Arrange
        String orderId = "order-123";
        OrderDto.OrderItemRequest itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId("menu-2")
                .menuItemName("Pizza")
                .price(75000.0)
                .quantity(1)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderIdAndMenuItemId(orderId, "menu-2")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        OrderDto.OrderResponse result = orderService.addItemToOrder(orderId, itemRequest);

        // Assert
        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void addItemToOrder_Success_WithExistingItem() {
        // Arrange
        String orderId = "order-123";
        String menuItemId = "menu-1";
        OrderDto.OrderItemRequest itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId(menuItemId)
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(1)
                .build();

        OrderItem existingItem = OrderItem.builder()
                .id("item-1")
                .menuItemId(menuItemId)
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .order(order)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderIdAndMenuItemId(orderId, menuItemId)).thenReturn(Optional.of(existingItem));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        OrderDto.OrderResponse result = orderService.addItemToOrder(orderId, itemRequest);

        // Assert
        assertNotNull(result);
        verify(orderItemRepository).save(existingItem);
        assertEquals(3, existingItem.getQuantity()); // Original 2 + new 1
    }

    @Test
    void addItemToOrder_ThrowsException_WhenOrderNotPending() {
        // Arrange
        String orderId = "order-123";
        order.setStatus(OrderStatus.DELIVERED);
        OrderDto.OrderItemRequest itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId("menu-2")
                .menuItemName("Pizza")
                .price(75000.0)
                .quantity(1)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.addItemToOrder(orderId, itemRequest));
        assertEquals("Cannot add items to an order that is DELIVERED", exception.getMessage());
    }

    @Test
    void updateOrderItem_Success() {
        // Arrange
        String orderId = "order-123";
        String itemId = "item-1";
        OrderDto.UpdateOrderItemRequest updateRequest = OrderDto.UpdateOrderItemRequest.builder()
                .quantity(3)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .id(itemId)
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .build();

        order.addOrderItem(orderItem);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        OrderDto.OrderResponse result = orderService.updateOrderItem(orderId, itemId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(3, orderItem.getQuantity());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderItem_ThrowsException_WhenOrderNotPending() {
        // Arrange
        String orderId = "order-123";
        String itemId = "item-1";
        order.setStatus(OrderStatus.DELIVERED);
        OrderDto.UpdateOrderItemRequest updateRequest = OrderDto.UpdateOrderItemRequest.builder()
                .quantity(3)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.updateOrderItem(orderId, itemId, updateRequest));
        assertEquals("Cannot update items in an order that is DELIVERED", exception.getMessage());
    }

    @Test
    void updateOrderItem_ThrowsException_WhenItemNotFound() {
        // Arrange
        String orderId = "order-123";
        String itemId = "nonexistent-item";
        OrderDto.UpdateOrderItemRequest updateRequest = OrderDto.UpdateOrderItemRequest.builder()
                .quantity(3)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.updateOrderItem(orderId, itemId, updateRequest));
        assertEquals("Order item not found with ID: " + itemId, exception.getMessage());
    }

    @Test
    void removeItemFromOrder_Success() {
        // Arrange
        String orderId = "order-123";
        String itemId = "item-1";

        OrderItem orderItem = OrderItem.builder()
                .id(itemId)
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .build();

        order.addOrderItem(orderItem);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        OrderDto.OrderResponse result = orderService.removeItemFromOrder(orderId, itemId);

        // Assert
        assertNotNull(result);
        verify(orderItemRepository).delete(orderItem);
        verify(orderRepository).save(order);
    }

    @Test
    void removeItemFromOrder_ThrowsException_WhenOrderNotPending() {
        // Arrange
        String orderId = "order-123";
        String itemId = "item-1";
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.removeItemFromOrder(orderId, itemId));
        assertEquals("Cannot remove items from an order that is DELIVERED", exception.getMessage());
    }

    @Test
    void removeItemFromOrder_ThrowsException_WhenItemNotFound() {
        // Arrange
        String orderId = "order-123";
        String itemId = "nonexistent-item";

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.removeItemFromOrder(orderId, itemId));
        assertEquals("Order item not found with ID: " + itemId, exception.getMessage());
    }

    @Test
    void updateOrderStatus_Success() {
        // Arrange
        String orderId = "order-123";
        OrderDto.UpdateOrderRequest updateRequest = OrderDto.UpdateOrderRequest.builder()
                .status(OrderStatus.DELIVERED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        // Act
        OrderDto.OrderResponse result = orderService.updateOrderStatus(orderId, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
        verify(mejaService).setMejaStatus(mejaId, MejaStatus.TERSEDIA);
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderStatus_ThrowsException_WhenOrderAlreadyDelivered() {
        // Arrange
        String orderId = "order-123";
        order.setStatus(OrderStatus.DELIVERED);
        OrderDto.UpdateOrderRequest updateRequest = OrderDto.UpdateOrderRequest.builder()
                .status(OrderStatus.CANCELLED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.updateOrderStatus(orderId, updateRequest));
        assertEquals("Cannot update status of an order that is already DELIVERED", exception.getMessage());
    }

    @Test
    void deleteOrder_Success_WhenPendingOrder() {
        // Arrange
        String orderId = "order-123";
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(orderId);

        // Assert
        verify(mejaService).setMejaStatus(mejaId, MejaStatus.TERSEDIA);
        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_Success_WhenCompletedOrder() {
        // Arrange
        String orderId = "order-123";
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        orderService.deleteOrder(orderId);

        // Assert
        verify(mejaService, never()).setMejaStatus(any(), any());
        verify(orderRepository).delete(order);
    }
}