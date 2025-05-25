package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutServiceImpl;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.MenuItemQuantityValidator;
import id.ac.ui.cs.advprog.ohioorder.pesanan.client.MenuServiceClient;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DraftStateTest {
    private Checkout checkout;
    private Order order;
    private ApplicationContext mockContext;
    private CheckoutServiceImpl mockCheckoutService;
    private MenuItemQuantityValidator mockValidator;
    private MenuServiceClient mockMenuServiceClient;

    @BeforeEach
    void setUp() {
        checkout = new Checkout();
        order = spy(new Order());
        order.setOrderItems(new ArrayList<>());
        checkout.setOrder(order);
        checkout.setState(CheckoutStateType.DRAFT);

        mockContext = mock(ApplicationContext.class);
        mockCheckoutService = mock(CheckoutServiceImpl.class);
        mockValidator = mock(MenuItemQuantityValidator.class);
        mockMenuServiceClient = mock(MenuServiceClient.class);

        when(mockContext.getBean(CheckoutService.class)).thenReturn(mockCheckoutService);
        when(mockValidator.validateOrderItemsQuantity(order)).thenReturn(List.of());

        DraftState.getInstance().setApplicationContext(mockContext);
    }

    @Test
    void testAdvanceTransitionToOrdered() {
        checkout.advance();
        assertEquals(CheckoutStateType.ORDERED, checkout.getState());
        assertEquals(checkout.getCheckoutState(), OrderedState.getInstance());
    }

    @Test
    void testCancelTransitionToCancelled() {
        checkout.cancel();
        assertEquals(CheckoutStateType.CANCELLED, checkout.getState());
    }

    @Test
    void advance_LocksOrder_WhenTransitioningToOrdered() {
        assertFalse(order.getLocked());

        doNothing().when(mockCheckoutService).validateQuantitiesBeforeNextState(checkout);
        doNothing().when(mockCheckoutService).reduceMenuItemQuantities(checkout);

        checkout.advance();

        verify(order).setLocked(true);
        assertEquals(CheckoutStateType.ORDERED, checkout.getState());
    }
}
