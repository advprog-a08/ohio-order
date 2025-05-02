package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class PreparingState implements CheckoutState {
    private static final PreparingState INSTANCE = new PreparingState();
    private PreparingState() {}

    public static PreparingState getInstance() {
        return INSTANCE;
    }

    @Override
    public void next(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.READY);
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot cancel order in preparing state");
    }

    @Override
    public void update() throws InvalidStateTransitionException {
        throw new InvalidStateTransitionException("Cannot update order in cancelled state");
    }
}
