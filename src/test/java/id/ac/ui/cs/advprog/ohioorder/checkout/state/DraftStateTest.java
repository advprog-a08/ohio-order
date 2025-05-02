package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

import static org.junit.jupiter.api.Assertions.*;

public class DraftStateTest {
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.DRAFT);
        checkout.initializeState();
    }

    @Test
    void testNextTransitionToCompleted() {
        checkout.nextState();
        assertEquals(CheckoutStateType.ORDERED, checkout.getState());
        assertEquals(checkout.getCheckoutState(), OrderedState.getInstance());
    }

    @Test
    void testCancelTransitionToCancelled() {
        checkout.cancel();
        assertEquals(CheckoutStateType.CANCELLED, checkout.getState());
    }

    @Test
    void testUpdateAccepted() {
        assertDoesNotThrow(() -> checkout.update());
    }
}
