package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.pesanan.client.MenuServiceClient;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuItemDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuServiceResponse;
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
    
    @Mock
    private MenuServiceClient menuServiceClient;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID mejaId;
    private UUID orderId;
    private Order order;
    private OrderDto.OrderRequest orderRequest;
    private OrderDto.OrderResponse orderResponse;
    private Meja meja;
    private MejaResponse mejaResponse;
    private MenuServiceResponse menuServiceResponse;
    private MenuItemDto menuItemDto;

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
        order = spy(Order.builder()
                .id(orderId)
                .meja(meja)
                .orderItems(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());

        orderRequest = OrderDto.OrderRequest.builder()
                .mejaId(mejaId)
                .items(List.of(
                        OrderDto.OrderItemRequest.builder()
                                .menuItemId("menu-1")
                                .quantity(2)
                                .build()
                ))
                .build();

        menuItemDto = MenuItemDto.builder()
                .id("menu-1")
                .name("Burger")
                .description("Delicious burger")
                .price(50000.0)
                .build();
                
        menuServiceResponse = new MenuServiceResponse();
        menuServiceResponse.setSuccess(true);
        menuServiceResponse.setMessage("Success");
        menuServiceResponse.setData(menuItemDto);

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
        when(menuServiceClient.verifyMenuItemExists("menu-1")).thenReturn(true);
        when(orderMapper.toEntity(orderRequest)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);
        when(menuServiceClient.getMenuItem("menu-1")).thenReturn(menuServiceResponse);

        OrderDto.OrderResponse result = orderService.createOrder(orderRequest);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(mejaId, result.getMejaId());
        verify(menuServiceClient).verifyMenuItemExists("menu-1");
        verify(orderRepository).save(order);
    }

    @Test
    void createOrder_ThrowsException_WhenMenuItemNotFound() {
        when(mejaService.getMejaById(mejaId)).thenReturn(mejaResponse);
        when(menuServiceClient.verifyMenuItemExists("menu-1")).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.createOrder(orderRequest));
        assertEquals("Menu item not found with ID: menu-1", exception.getMessage());
        verify(orderRepository, never()).save(any());
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
        when(menuServiceClient.getMenuItem("menu-1")).thenReturn(menuServiceResponse);

        List<OrderDto.OrderResponse> result = orderService.getOrdersByMejaId(mejaId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderId, result.getFirst().getId());
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponse);
        when(menuServiceClient.getMenuItem("menu-1")).thenReturn(menuServiceResponse);

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
                .quantity(1)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(menuServiceClient.verifyMenuItemExists("menu-2")).thenReturn(true);
        when(orderItemRepository.findByOrderIdAndMenuItemId(orderId, "menu-2")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);
        when(menuServiceClient.getMenuItem("menu-1")).thenReturn(menuServiceResponse);

        OrderDto.OrderResponse result = orderService.addItemToOrder(orderId, itemRequest);

        assertNotNull(result);
        verify(menuServiceClient).verifyMenuItemExists("menu-2");
        verify(orderRepository).save(any(Order.class));
        
        verify(order).addOrderItem(argThat(orderItem ->
                orderItem.getMenuItemId().equals("menu-2") &&
                orderItem.getQuantity() == 1));
    }

    @Test
    void addItemToOrder_Success_WithExistingItem() {
        UUID orderId = UUID.randomUUID();
        String menuItemId = "menu-1";

        OrderDto.OrderItemRequest itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId(menuItemId)
                .quantity(1)
                .build();

        OrderItem existingItem = OrderItem.builder()
                .id(UUID.randomUUID())
                .menuItemId(menuItemId)
                .quantity(2)
                .order(order)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(menuServiceClient.verifyMenuItemExists(menuItemId)).thenReturn(true);
        when(orderItemRepository.findByOrderIdAndMenuItemId(orderId, menuItemId)).thenReturn(Optional.of(existingItem));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);
        when(menuServiceClient.getMenuItem(menuItemId)).thenReturn(menuServiceResponse);

        OrderDto.OrderResponse result = orderService.addItemToOrder(orderId, itemRequest);

        assertNotNull(result);
        verify(orderItemRepository).save(existingItem);
        assertEquals(3, existingItem.getQuantity());
    }
    
    @Test
    void addItemToOrder_ThrowsException_WhenMenuItemNotFound() {
        UUID orderId = UUID.randomUUID();
        OrderDto.OrderItemRequest itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId("menu-nonexistent")
                .quantity(1)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(menuServiceClient.verifyMenuItemExists("menu-nonexistent")).thenReturn(false);

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> orderService.addItemToOrder(orderId, itemRequest));
        assertEquals("Menu item not found with ID: menu-nonexistent", exception.getMessage());
        verify(orderRepository, never()).save(any());
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
    
    @Test
    void enrichOrderResponse_HandlesDeletedMenuItems() {
        OrderDto.OrderResponse response = OrderDto.OrderResponse.builder()
                .id(orderId)
                .mejaId(mejaId)
                .nomorMeja("A1")
                .items(List.of(
                        OrderDto.OrderItemResponse.builder()
                                .id(UUID.randomUUID())
                                .menuItemId("deleted-menu-item")
                                .quantity(2)
                                .build()
                ))
                .build();
                
        when(menuServiceClient.getMenuItem("deleted-menu-item"))
                .thenThrow(new NoSuchElementException("Menu item not found"));

        OrderDto.OrderResponse result = orderService.enrichOrderResponse(response);

        assertNotNull(result);
        OrderDto.OrderItemResponse itemResponse = result.getItems().getFirst();
        assertEquals("[Unavailable Item]", itemResponse.getMenuItemName());
        assertEquals("This menu item is Unavailable.", itemResponse.getMenuItemDescription());
        assertEquals("Unavailable", itemResponse.getMenuItemCategory());
        assertEquals(0.0, itemResponse.getPrice());
        assertEquals(0.0, itemResponse.getSubtotal());
    }
}