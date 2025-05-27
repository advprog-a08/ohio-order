package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public final class OrderedState extends CheckoutState {
    private static final OrderedState INSTANCE = new OrderedState();
    private OrderedState() {}

    public static OrderedState getInstance() {
        return INSTANCE;
    }

    @Override
    public void advance(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.PREPARING);
    }

    @Override
    public String message() {
        return "Your order has been placed! The kitchen is getting started.";
    }
}
