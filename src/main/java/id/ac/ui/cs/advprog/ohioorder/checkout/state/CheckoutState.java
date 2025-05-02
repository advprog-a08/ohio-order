package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public interface CheckoutState {
    void next(Checkout checkout) throws InvalidStateTransitionException;
    void update() throws InvalidStateTransitionException;
    void cancel(Checkout checkout) throws InvalidStateTransitionException;
}
