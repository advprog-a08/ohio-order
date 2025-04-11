package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreparingStateTest {
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.PREPARING);
        checkout.initializeState();
    }

    @Test
    void testNextTransitionToCompleted() {
        checkout.nextState();
        assertEquals(CheckoutStateType.READY, checkout.getState());
    }

    @Test
    void testCancelTransitionToCancelled() {
        assertThrows(InvalidStateTransitionException.class, () -> checkout.cancel());
    }
}
