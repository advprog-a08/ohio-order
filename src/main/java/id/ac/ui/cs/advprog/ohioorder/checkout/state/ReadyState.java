package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class ReadyState extends CheckoutState {
    private static final ReadyState INSTANCE = new ReadyState();
    private ReadyState() {}

    public static ReadyState getInstance() {
        return INSTANCE;
    }

    @Override
    public void advance(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.COMPLETED);
    }

    @Override
    public String message() {
        return "Your order is ready! Please head to the counter to pick it up.";
    }
}
