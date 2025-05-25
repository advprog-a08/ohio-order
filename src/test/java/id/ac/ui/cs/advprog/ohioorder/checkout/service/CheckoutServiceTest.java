package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InsufficientQuantityException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.client.MenuServiceClient;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    private MenuItemQuantityValidator quantityValidator;

    @Mock
    private MenuServiceClient menuServiceClient;

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

    @Test
    void reduceMenuItemQuantitiesSuccess() {
        when(menuServiceClient.reduceMenuItemQuantityAsync("menu-1", 2))
                .thenReturn(CompletableFuture.completedFuture(true));
        when(menuServiceClient.reduceMenuItemQuantityAsync("menu-2", 3))
                .thenReturn(CompletableFuture.completedFuture(true));

        assertDoesNotThrow(() -> checkoutService.reduceMenuItemQuantities(checkout));

        verify(menuServiceClient).reduceMenuItemQuantityAsync("menu-1", 2);
        verify(menuServiceClient).reduceMenuItemQuantityAsync("menu-2", 3);
    }
}