package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CancelledStateTest {
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.CANCELLED);
        checkout.initializeState();
    }

    @Test
    void testNextTransitionToCompleted() {
        assertThrows(InvalidStateTransitionException.class, () -> checkout.nextState());
    }

    @Test
    void testCancelTransitionToCancelled() {
        assertThrows(InvalidStateTransitionException.class, () -> checkout.cancel());
    }

    @Test
    void testUpdateRejected() {
        assertThrows(InvalidStateTransitionException.class, () -> checkout.update());
    }
}
