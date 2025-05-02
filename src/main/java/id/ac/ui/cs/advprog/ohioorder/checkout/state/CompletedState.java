package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class CompletedState implements CheckoutState {
    private static final CompletedState INSTANCE = new CompletedState();
    private CompletedState() {}

    public static CompletedState getInstance() {
        return INSTANCE;
    }

    @Override
    public void next(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot proceed order in completed state");
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot cancel order in completed state");
    }
}
