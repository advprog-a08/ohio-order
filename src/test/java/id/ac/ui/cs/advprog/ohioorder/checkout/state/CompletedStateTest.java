package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import static org.junit.jupiter.api.Assertions.assertThrows;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class CompletedStateTest {
    private Checkout checkout;

    @BeforeEach
    void setUp() {
        checkout = new Checkout();
        checkout.setState(CheckoutStateType.COMPLETED);
        checkout.initializeState();
    }

    @Test
    void testNextTransitionNoChange() {
        assertThrows(InvalidStateTransitionException.class, () -> checkout.nextState());
    }

    @Test
    void testCancelTransitionNoChange() {
        assertThrows(InvalidStateTransitionException.class, () -> checkout.cancel());
    }
}
