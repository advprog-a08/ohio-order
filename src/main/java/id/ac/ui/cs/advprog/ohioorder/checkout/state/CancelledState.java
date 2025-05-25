package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class CancelledState implements CheckoutState {
    private static final CancelledState INSTANCE = new CancelledState();
    private CancelledState() {}

    public static CancelledState getInstance() {
        return INSTANCE;
    }

    @Override
    public void advance(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot proceed order in cancelled state");
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot cancel order in cancelled state");
    }

    @Override
    public String message() {
        return "Your order has been canceled. No worries — we hope to serve you next time!";
    }
}
