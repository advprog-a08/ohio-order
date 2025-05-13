package id.ac.ui.cs.advprog.ohioorder.checkout.model;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CancelledState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CompletedState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.DraftState;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class CheckoutTest {
    private Order order;
    private Meja meja;
    private Checkout checkout;
    private ArrayList<OrderItem> orderItems;

    @BeforeEach
    void setUp() {
        orderItems = new ArrayList<>();

        meja = Meja.builder()
                .id(UUID.randomUUID())
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        order = Order.builder()
                .id("order-123")
                .meja(meja)
                .orderItems(orderItems)
                .build();

        orderItems.add(OrderItem.builder()
                .id("item-1")
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .build());

        orderItems.add(OrderItem.builder()
                .id("item-2")
                .menuItemId("menu-2")
                .menuItemName("Pizza")
                .price(75000.0)
                .quantity(1)
                .build());

        checkout = new Checkout();
        checkout.setOrder(order);
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forDraft() {
        checkout.setState(CheckoutStateType.DRAFT);
        checkout.initializeState();

        assertInstanceOf(DraftState.class, checkout.getCheckoutState());
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forCompleted() {
        checkout.setState(CheckoutStateType.COMPLETED);
        checkout.initializeState();

        assertInstanceOf(CompletedState.class, checkout.getCheckoutState());
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forCancelled() {
        checkout.setState(CheckoutStateType.CANCELLED);
        checkout.initializeState();

        assertInstanceOf(CancelledState.class, checkout.getCheckoutState());
    }

    @Test
    void testSetOrder_setsOrderCorrectly() {
        Order order = new Order();
        checkout.setOrder(order);
        assertEquals(order, checkout.getOrder());
    }

    @Test
    void testCalculateTotal_calculatesTotalCorrectly() {
        double expected = orderItems.stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .sum();
        assertEquals(expected, checkout.calculateTotal());
    }
}
