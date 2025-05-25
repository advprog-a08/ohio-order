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
                .id(UUID.randomUUID())
                .meja(meja)
                .orderItems(orderItems)
                .build();

        orderItems.add(OrderItem.builder()
                .id(UUID.randomUUID())
                .menuItemId("menu-1")
                .quantity(2)
                .build());

        orderItems.add(OrderItem.builder()
                .id(UUID.randomUUID())
                .menuItemId("menu-2")
                .quantity(1)
                .build());

        checkout = new Checkout();
        checkout.setOrder(order);
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forDraft() {
        checkout.setState(CheckoutStateType.DRAFT);

        assertInstanceOf(DraftState.class, checkout.getCheckoutState());
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forCompleted() {
        checkout.setState(CheckoutStateType.COMPLETED);

        assertInstanceOf(CompletedState.class, checkout.getCheckoutState());
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forCancelled() {
        checkout.setState(CheckoutStateType.CANCELLED);

        assertInstanceOf(CancelledState.class, checkout.getCheckoutState());
    }

    @Test
    void testMessage_draftState_getsCorrectMessage() {
        checkout.setState(CheckoutStateType.DRAFT);

        assertEquals(checkout.message(), CheckoutStateType.DRAFT.getCheckoutState().message());
    }

    @Test
    void testMessage_orderedState_getsCorrectMessage() {
        checkout.setState(CheckoutStateType.ORDERED);

        assertEquals(checkout.message(), CheckoutStateType.ORDERED.getCheckoutState().message());
    }

    @Test
    void testMessage_preparingState_getsCorrectMessage() {
        checkout.setState(CheckoutStateType.PREPARING);

        assertEquals(checkout.message(), CheckoutStateType.PREPARING.getCheckoutState().message());
    }

    @Test
    void testMessage_completedState_getsCorrectMessage() {
        checkout.setState(CheckoutStateType.COMPLETED);

        assertEquals(checkout.message(), CheckoutStateType.COMPLETED.getCheckoutState().message());
    }

    @Test
    void testMessage_readyState_getsCorrectMessage() {
        checkout.setState(CheckoutStateType.READY);

        assertEquals(checkout.message(), CheckoutStateType.READY.getCheckoutState().message());
    }

    @Test
    void testMessage_cancelledState_getsCorrectMessage() {
        checkout.setState(CheckoutStateType.CANCELLED);

        assertEquals(checkout.message(), CheckoutStateType.CANCELLED.getCheckoutState().message());
    }

    @Test
    void testSetOrder_setsOrderCorrectly() {
        Order order = new Order();
        checkout.setOrder(order);
        assertEquals(order, checkout.getOrder());
    }
}
