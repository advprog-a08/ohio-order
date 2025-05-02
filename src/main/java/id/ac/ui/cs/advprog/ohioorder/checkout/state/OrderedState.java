package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class OrderedState implements CheckoutState {
    private static final OrderedState INSTANCE = new OrderedState();
    private OrderedState() {}

    public static OrderedState getInstance() {
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

    @Override
    public void update() throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot update order in cancelled state");
    }
}
