package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import static org.junit.jupiter.api.Assertions.assertThrows;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class CompletedStateTest {
    private CheckoutState state;
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        state = CompletedState.getInstance();
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.COMPLETED);
    }

    @Test
    void testNextTransitionNoChange() {
        assertThrows(InvalidStateTransitionException.class, () -> state.next(checkout));
    }

    @Test
    void testCancelTransitionNoChange() {
        assertThrows(InvalidStateTransitionException.class, () -> state.cancel(checkout));
    }
}
