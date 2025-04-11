package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class CompletedStateTest {
    private CheckoutState state;
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        state = new CompletedState();
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.COMPLETED);
    }

    @Test
    void testNextTransitionNoChange() {
        state.next(checkout);
        assertEquals(CheckoutStateType.COMPLETED, checkout.getState());
    }

    @Test
    void testCancelTransitionNoChange() {
        state.cancel(checkout);
        assertEquals(CheckoutStateType.COMPLETED, checkout.getState());
    }
}
