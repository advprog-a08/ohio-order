package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

@Component
public class DraftState implements CheckoutState {
    @Override
    public void next(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.COMPLETED);
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.CANCELLED);
    }
}