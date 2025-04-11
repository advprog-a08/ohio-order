package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class DraftStateTest {
    private CheckoutState state;
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        state = DraftState.getInstance();
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.DRAFT);
    }

    @Test
    void testNextTransitionToCompleted() {
        state.next(checkout);
        assertEquals(CheckoutStateType.COMPLETED, checkout.getState());
    }

    @Test
    void testCancelTransitionToCancelled() {
        state.cancel(checkout);
        assertEquals(CheckoutStateType.CANCELLED, checkout.getState());
    }
}
