package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InsufficientQuantityException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.client.MenuServiceClient;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderMapper;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutServiceTest {

    @Mock
    private CheckoutRepository checkoutRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private MenuItemQuantityValidator quantityValidator;

    @Mock
    private MenuServiceClient menuServiceClient;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    private Checkout checkout;
    private Order order;
    private UUID orderId;
    private UUID checkoutId;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        checkoutId = UUID.randomUUID();

        order = new Order();
        order.setId(orderId);
        order.setOrderItems(new ArrayList<>());

        OrderItem orderItem1 = OrderItem.builder()
                .id(UUID.randomUUID())
                .menuItemId("menu-1")
                .quantity(2)
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .id(UUID.randomUUID())
                .menuItemId("menu-2")
                .quantity(3)
                .build();

        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        checkout = new Checkout();
        checkout.setId(checkoutId);
        checkout.setOrder(order);
    }

    @Test
    void testSave() {
        when(checkoutRepository.save(checkout)).thenReturn(checkout);

        Checkout saved = checkoutService.save(checkout);

        assertEquals(checkout, saved);
        verify(checkoutRepository).save(checkout);
    }

    @Test
    void testCreate() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(checkoutRepository.save(any(Checkout.class))).thenAnswer(i -> i.getArgument(0));

        Optional<Checkout> result = checkoutService.create(orderId);

        assertTrue(result.isPresent());
        assertEquals(order, result.get().getOrder());
        verify(orderRepository).findById(orderId);
        verify(checkoutRepository).save(any(Checkout.class));
    }

    @Test
    void testCreateOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Optional<Checkout> result = checkoutService.create(orderId);

        assertFalse(result.isPresent());
        verify(orderRepository).findById(orderId);
        verify(checkoutRepository, never()).save(any(Checkout.class));
    }

    @Test
    void testFindByIdFound() {
        when(checkoutRepository.findById(checkoutId)).thenReturn(Optional.of(checkout));

        Optional<Checkout> result = checkoutService.findById(checkoutId.toString());

        assertTrue(result.isPresent());
        assertEquals(checkout, result.get());
        verify(checkoutRepository).findById(checkoutId);
    }

    @Test
    void testFindAllEmpty() {
        when(checkoutRepository.findAllOrderByOrderCreatedAt()).thenReturn(List.of());

        List<Checkout> result = checkoutService.findAll();

        assertTrue(result.isEmpty());
        verify(checkoutRepository).findAllOrderByOrderCreatedAt();
    }

    @Test
    void testFindAllFound() {
        when(checkoutRepository.findAllOrderByOrderCreatedAt()).thenReturn(List.of(checkout));

        List<Checkout> result = checkoutService.findAll();

        assertEquals(1, result.toArray().length);
        assertEquals(checkout, result.getFirst());
        verify(checkoutRepository).findAllOrderByOrderCreatedAt();
    }

    @Test
    void testFindByIdNotFound() {
        when(checkoutRepository.findById(checkoutId)).thenReturn(Optional.empty());

        Optional<Checkout> result = checkoutService.findById(checkoutId.toString());

        assertFalse(result.isPresent());
        verify(checkoutRepository).findById(checkoutId);
    }

    @Test
    void validateQuantitiesBeforeAdvance_Success() {
        when(quantityValidator.validateOrderItemsQuantity(order)).thenReturn(List.of());

        assertDoesNotThrow(() -> checkoutService.validateQuantitiesBeforeNextState(checkout));

        verify(quantityValidator).validateOrderItemsQuantity(order);
    }

    @Test
    void validateQuantitiesBeforeAdvance_ThrowsException_WhenInsufficientQuantity() {
        when(quantityValidator.validateOrderItemsQuantity(order))
                .thenReturn(List.of("Insufficient quantity for menu item 'Pizza'"));

        InsufficientQuantityException exception = assertThrows(
                InsufficientQuantityException.class,
                () -> checkoutService.validateQuantitiesBeforeNextState(checkout)
        );

        assertTrue(exception.getMessage().contains("Insufficient quantity"));
        verify(quantityValidator).validateOrderItemsQuantity(order);
    }

    private Checkout mockCheckoutWithItems(String... itemIds) {
        Checkout checkout = new Checkout();
        Order order = new Order();

        List<OrderItem> items = Arrays.stream(itemIds)
                .map(id -> {
                    OrderItem item = new OrderItem();
                    item.setMenuItemId(id);
                    item.setQuantity(1);
                    return item;
                })
                .collect(Collectors.toList());

        order.setOrderItems(items);
        checkout.setOrder(order);
        return checkout;
    }

    @Test
    void testReduceMenuItemQuantities_AllSuccess() {
        Checkout checkout = mockCheckoutWithItems("item1", "item2");

        for (OrderItem item : checkout.getOrder().getOrderItems()) {
            when(menuServiceClient.reduceMenuItemQuantityAsync(item.getMenuItemId(), item.getQuantity()))
                    .thenReturn(CompletableFuture.completedFuture(true));
        }

        assertDoesNotThrow(() -> checkoutService.reduceMenuItemQuantities(checkout));
    }

    @Test
    void testReduceMenuItemQuantities_SomeFailures() {
        Checkout checkout = mockCheckoutWithItems("item1", "item2");

        when(menuServiceClient.reduceMenuItemQuantityAsync("item1", 1))
                .thenReturn(CompletableFuture.completedFuture(true));
        when(menuServiceClient.reduceMenuItemQuantityAsync("item2", 1))
                .thenReturn(CompletableFuture.completedFuture(false));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkoutService.reduceMenuItemQuantities(checkout);
        });

        assertTrue(exception.getMessage().contains("item2"));
    }

    @Test
    void testReduceMenuItemQuantities_ThrowsExecutionException() {
        Checkout checkout = mockCheckoutWithItems("item1");

        CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Service down"));

        when(menuServiceClient.reduceMenuItemQuantityAsync("item1", 1))
                .thenReturn(failedFuture);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            checkoutService.reduceMenuItemQuantities(checkout);
        });

        assertTrue(exception.getMessage().contains("Error reducing menu item quantities"));
    }

    @Test
    void testReduceMenuItemQuantities_InterruptedException() throws Exception {
        Checkout checkout = mockCheckoutWithItems("item1");

        CompletableFuture<Boolean> interruptedFuture = Mockito.mock(CompletableFuture.class);
        when(interruptedFuture.thenApply(any())).thenCallRealMethod();
        when(menuServiceClient.reduceMenuItemQuantityAsync("item1", 1))
                .thenReturn(interruptedFuture);

        // Spy to override futures array to call get() on our mock
        CheckoutServiceImpl serviceSpy = Mockito.spy(checkoutService);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            serviceSpy.reduceMenuItemQuantities(checkout);
        });
    }

    @Test
    void findAllFormatted() {
        when(checkoutRepository.findAllOrderByOrderCreatedAt()).thenReturn(List.of(checkout));

        OrderDto.OrderResponse initialOrderResponse = new OrderDto.OrderResponse();
        OrderDto.OrderResponse enrichedOrderResponse = new OrderDto.OrderResponse();

        when(orderMapper.toDto(order)).thenReturn(initialOrderResponse);
        when(orderService.enrichOrderResponseAsync(initialOrderResponse))
                .thenReturn(CompletableFuture.completedFuture(enrichedOrderResponse));

        List<CheckoutResponse> result = checkoutService.findAllFormatted();

        assertEquals(1, result.size());
    }
}