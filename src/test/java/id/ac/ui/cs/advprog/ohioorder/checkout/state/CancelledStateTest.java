package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CancelledStateTest {
    private CheckoutState state;
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        state = CancelledState.getInstance();
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.CANCELLED);
    }

    @Test
    void testNextTransitionToCompleted() {
        assertThrows(InvalidStateTransitionException.class, () -> state.next(checkout));
    }

    @Test
    void testCancelTransitionToCancelled() {
        assertThrows(InvalidStateTransitionException.class, () -> state.cancel(checkout));
    }
}
