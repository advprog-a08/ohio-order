package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CancelledStateTest {
    private CheckoutState state;
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        state = new CancelledState();
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.CANCELLED);
    }

    @Test
    void testNextTransitionToCompleted() {
        state.next(checkout);
        assertEquals(CheckoutStateType.CANCELLED, checkout.getState());
    }

    @Test
    void testCancelTransitionToCancelled() {
        state.cancel(checkout);
        assertEquals(CheckoutStateType.CANCELLED, checkout.getState());
    }
}
