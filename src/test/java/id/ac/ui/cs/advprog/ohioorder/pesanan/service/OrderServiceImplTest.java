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
import java.util.concurrent.CompletableFuture;

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
                .quantity(10)
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
        when(menuServiceClient.getMenuItem("menu-1")).thenReturn(menuServiceResponse);
        when(orderMapper.toEntity(orderRequest)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        when(menuServiceClient.getMultipleMenuItemsAsync(anyList())).thenReturn(
                CompletableFuture.completedFuture(List.of(menuServiceResponse)));

        CompletableFuture<OrderDto.OrderResponse> futureResult = orderService.createOrder(orderRequest);
        OrderDto.OrderResponse result = futureResult.join();

        assertNotNull(result);
        assertEquals(orderId, result.getId());
        assertEquals(mejaId, result.getMejaId());

        verify(orderRepository).save(order);
    }

    @Test
    void createOrder_ThrowsException_WhenMenuItemNotFound() {
        when(mejaService.getMejaById(mejaId)).thenReturn(mejaResponse);
        when(menuServiceClient.getMenuItem("menu-1"))
            .thenThrow(new NoSuchElementException("Menu item not found with ID: menu-1"));

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
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByMejaId(mejaId)).thenReturn(orders);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        when(menuServiceClient.getMultipleMenuItemsAsync(anyList())).thenReturn(
                CompletableFuture.completedFuture(List.of(menuServiceResponse)));

        List<CompletableFuture<OrderDto.OrderResponse>> results = orderService.getOrdersByMejaId(mejaId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(orderResponse, results.get(0).join());
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        when(menuServiceClient.getMultipleMenuItemsAsync(anyList())).thenReturn(
                CompletableFuture.completedFuture(List.of(menuServiceResponse)));

        CompletableFuture<OrderDto.OrderResponse> futureResult = orderService.getOrderById(orderId);
        OrderDto.OrderResponse result = futureResult.join();

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

        MenuItemDto menuItem2 = MenuItemDto.builder()
                .id("menu-2")
                .name("Pizza")
                .description("Delicious pizza")
                .price(60000.0)
                .quantity(5)
                .build();

        MenuServiceResponse menu2Response = new MenuServiceResponse();
        menu2Response.setSuccess(true);
        menu2Response.setMessage("Success");
        menu2Response.setData(menuItem2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(menuServiceClient.getMenuItem("menu-2")).thenReturn(menu2Response);
        when(orderItemRepository.findByOrderIdAndMenuItemId(orderId, "menu-2")).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        when(menuServiceClient.getMultipleMenuItemsAsync(anyList())).thenReturn(
                CompletableFuture.completedFuture(List.of(menuServiceResponse)));

        CompletableFuture<OrderDto.OrderResponse> futureResult = orderService.addItemToOrder(orderId, itemRequest);
        OrderDto.OrderResponse result = futureResult.join();

        assertNotNull(result);
        verify(menuServiceClient).getMenuItem("menu-2");
        verify(orderRepository).save(any(Order.class));

        verify(order).addOrderItem(argThat(orderItem ->
                orderItem.getMenuItemId().equals("menu-2") &&
                        orderItem.getQuantity() == 1));
    }

    @Test
    void addItemToOrder_Success_WithExistingItem() {
        UUID orderId = UUID.randomUUID();
        OrderDto.OrderItemRequest itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId("menu-1")
                .quantity(1)
                .build();

        OrderItem existingItem = OrderItem.builder()
                .id(UUID.randomUUID())
                .menuItemId("menu-1")
                .quantity(2)
                .order(order)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(menuServiceClient.getMenuItem("menu-1")).thenReturn(menuServiceResponse);
        when(orderItemRepository.findByOrderIdAndMenuItemId(orderId, "menu-1")).thenReturn(Optional.of(existingItem));
        when(orderItemRepository.save(existingItem)).thenReturn(existingItem);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponse);

        when(menuServiceClient.getMultipleMenuItemsAsync(anyList())).thenReturn(
                CompletableFuture.completedFuture(List.of(menuServiceResponse)));

        CompletableFuture<OrderDto.OrderResponse> futureResult = orderService.addItemToOrder(orderId, itemRequest);
        OrderDto.OrderResponse result = futureResult.join();

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

        when(menuServiceClient.getMenuItem("menu-nonexistent"))
            .thenThrow(new NoSuchElementException("Menu item not found with ID: menu-nonexistent"));

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

        when(menuServiceClient.getMultipleMenuItemsAsync(anyList())).thenReturn(
                CompletableFuture.completedFuture(List.of(menuServiceResponse)));

        CompletableFuture<OrderDto.OrderResponse> futureResult = orderService.updateOrderItem(orderId, itemId, updateRequest);
        OrderDto.OrderResponse result = futureResult.join();

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
                .id(UUID.randomUUID())
                .mejaId(mejaId)
                .nomorMeja("A1")
                .items(List.of(
                        OrderDto.OrderItemResponse.builder()
                                .id(UUID.randomUUID())
                                .menuItemId("deleted-menu-item")
                                .quantity(1)
                                .build()
                ))
                .build();

        when(menuServiceClient.getMultipleMenuItemsAsync(anyList())).thenReturn(
                CompletableFuture.completedFuture(Collections.emptyList()));

        CompletableFuture<OrderDto.OrderResponse> futureResult = orderService.enrichOrderResponseAsync(response);
        OrderDto.OrderResponse result = futureResult.join();

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        OrderDto.OrderItemResponse item = result.getItems().get(0);
        assertEquals("[Unavailable Item]", item.getMenuItemName());
        assertNotNull(item.getSubtotal());
    }

    @Test
    void addItemToOrder_ThrowsException_WhenOrderIsLocked() {
        UUID orderId = UUID.randomUUID();
        OrderDto.OrderItemRequest itemRequest = OrderDto.OrderItemRequest.builder()
                .menuItemId("menu-1")
                .quantity(1)
                .build();

        order.setLocked(true);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.addItemToOrder(orderId, itemRequest).join());
        assertEquals("Cannot modify order because it has been checked out", exception.getMessage());

        verify(orderItemRepository, never()).findByOrderIdAndMenuItemId(any(), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderItem_ThrowsException_WhenOrderIsLocked() {
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
        order.setLocked(true);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.updateOrderItem(orderId, itemId, updateRequest).join());
        assertEquals("Cannot modify order because it has been checked out", exception.getMessage());

        verify(orderRepository, never()).save(any());
    }

    @Test
    void removeItemFromOrder_ThrowsException_WhenOrderIsLocked() {
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        OrderItem orderItem = OrderItem.builder()
                .id(itemId)
                .menuItemId("menu-1")
                .quantity(2)
                .build();

        order.addOrderItem(orderItem);
        order.setLocked(true);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.removeItemFromOrder(orderId, itemId));
        assertEquals("Cannot modify order because it has been checked out", exception.getMessage());

        verify(orderItemRepository, never()).delete(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void deleteOrder_ThrowsException_WhenOrderIsLocked() {
        order.setLocked(true);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> orderService.deleteOrder(orderId));
        assertEquals("Cannot delete order because it has been checked out", exception.getMessage());

        verify(orderRepository, never()).delete(any());
    }
}