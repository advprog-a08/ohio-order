package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class CompletedStateTest {
    private final DraftState draftState = new DraftState();

    @Test
    void testNextTransitionNoChange() {
        Checkout checkout = new Checkout();
        checkout.setState(CheckoutStateType.COMPLETED);

        draftState.next(checkout);

        assertEquals(CheckoutStateType.COMPLETED, checkout.getState());
    }
}
