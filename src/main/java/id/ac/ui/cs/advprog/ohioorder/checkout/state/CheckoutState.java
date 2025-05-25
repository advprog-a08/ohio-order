package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public abstract class CheckoutState {
    public abstract String message();

    public void advance(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot advance in this state");
    }

    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot cancel in this state");
    }
}
