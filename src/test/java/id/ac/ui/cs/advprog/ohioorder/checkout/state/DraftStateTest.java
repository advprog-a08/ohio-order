package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class DraftStateTest {
    private final DraftState draftState = new DraftState();

    @Test
    void testNextTransitionToCompleted() {
        Checkout checkout = new Checkout();
        checkout.setState(CheckoutStateType.DRAFT);

        draftState.next(checkout);

        assertEquals(CheckoutStateType.COMPLETED, checkout.getState());
    }
}
