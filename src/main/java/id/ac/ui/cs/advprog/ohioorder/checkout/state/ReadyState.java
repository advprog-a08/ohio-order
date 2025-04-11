package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class ReadyState implements CheckoutState {
    private static ReadyState INSTANCE;
    private ReadyState() {}

    public static ReadyState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ReadyState();
        }

        return INSTANCE;
    }

    @Override
    public void next(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.COMPLETED);
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot cancel order in ready state");
    }
}
