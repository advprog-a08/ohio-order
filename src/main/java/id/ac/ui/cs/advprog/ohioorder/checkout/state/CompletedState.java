package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

@Component
public class CompletedState implements CheckoutState {
    @Override
    public void next(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot proceed order in completed state");
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot cancel order in completed state");
    }
}
