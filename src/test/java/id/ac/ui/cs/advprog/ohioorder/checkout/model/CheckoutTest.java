package id.ac.ui.cs.advprog.ohioorder.checkout.model;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CancelledState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CompletedState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.DraftState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class CheckoutTest {
    Checkout checkout;

    @BeforeEach
    public void setUp() {
        checkout = new Checkout();
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forDraft() {
        checkout.setState(CheckoutStateType.DRAFT);
        checkout.initializeState();

        assertInstanceOf(DraftState.class, checkout.getCheckoutState());
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forCompleted() {
        checkout.setState(CheckoutStateType.COMPLETED);
        checkout.initializeState();

        assertInstanceOf(CompletedState.class, checkout.getCheckoutState());
    }

    @Test
    void testInitializeState_setsCorrectStateInstance_forCancelled() {
        checkout.setState(CheckoutStateType.CANCELLED);
        checkout.initializeState();

        assertInstanceOf(CancelledState.class, checkout.getCheckoutState());
    }
}
