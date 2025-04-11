package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class OrderedState implements CheckoutState {
    private static OrderedState INSTANCE;
    private OrderedState() {}

    public static OrderedState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OrderedState();
        }

        return INSTANCE;
    }

    @Override
    public void next(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.PREPARING);
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot cancel order in ordered state");
    }
}
