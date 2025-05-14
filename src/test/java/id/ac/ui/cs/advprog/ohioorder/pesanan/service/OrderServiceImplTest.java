package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderMapper;
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
    private UUID orderId;
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

        orderId = UUID.randomUUID();
        order = Order.builder()
                .id(orderId)
                .meja(meja)
                .orderItems(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

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
                .id(orderId)
                .mejaId(mejaId)
                .nomorMeja("A1")
                .items(List.of(
                        OrderDto.OrderItemResponse.builder()
                                .id(UUID.randomUUID())
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
        when(orderMapper.toEntity(orderRequest)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        OrderDto.OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(mejaId, result.getMejaId());
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

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(orderRequest));
        assertEquals("Table is not available for ordering", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrdersByMejaId_Success() {
        when(orderRepository.findByMejaId(mejaId)).thenReturn(List.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        List<OrderDto.OrderResponse> result = orderService.getOrdersByMejaId(mejaId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderId, result.getFirst().getId());
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        OrderDto.OrderResponse result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void getOrderById_ThrowsException_WhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.getOrderById(orderId));
        assertEquals("Order not found with ID: " + orderId, exception.getMessage());
    }

    @Test
    void addItemToOrder_Success_WithNewItem() {
        UUID orderId = UUID.randomUUID();
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

        OrderDto.OrderResponse result = orderService.addItemToOrder(orderId, itemRequest);

        assertNotNull(result);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void addItemToOrder_Success_WithExistingItem() {
        UUID orderId = UUID.randomUUID();
        String menuItemId = "menu-1";

        OrderDto.OrderItemRequest itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId(menuItemId)
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(1)
                .build();

        OrderItem existingItem = OrderItem.builder()
                .id(UUID.randomUUID())
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

        OrderDto.OrderResponse result = orderService.addItemToOrder(orderId, itemRequest);

        assertNotNull(result);
        verify(orderItemRepository).save(existingItem);
        assertEquals(3, existingItem.getQuantity());
    }

    @Test
    void updateOrderItem_Success() {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
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

        OrderDto.OrderResponse result = orderService.updateOrderItem(orderId, itemId, updateRequest);

        assertNotNull(result);
        assertEquals(3, orderItem.getQuantity());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderItem_ThrowsException_WhenItemNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        OrderDto.UpdateOrderItemRequest updateRequest = OrderDto.UpdateOrderItemRequest.builder()
                .quantity(3)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.updateOrderItem(orderId, itemId, updateRequest));
        assertEquals("Order item not found with ID: " + itemId, exception.getMessage());
    }

    @Test
    void removeItemFromOrder_Success() {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

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

        OrderDto.OrderResponse result = orderService.removeItemFromOrder(orderId, itemId);

        assertNotNull(result);
        verify(orderItemRepository).delete(orderItem);
        verify(orderRepository).save(order);
    }

    @Test
    void removeItemFromOrder_ThrowsException_WhenItemNotFound() {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.removeItemFromOrder(orderId, itemId));
        assertEquals("Order item not found with ID: " + itemId, exception.getMessage());
    }

    @Test
    void deleteOrder_Success() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.deleteOrder(orderId);

        verify(orderRepository).delete(order);
    }
}