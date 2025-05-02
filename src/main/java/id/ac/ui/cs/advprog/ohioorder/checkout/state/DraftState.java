package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class DraftState implements CheckoutState {
    private static final DraftState INSTANCE = new DraftState();
    private DraftState() {}

    public static DraftState getInstance() {
        return INSTANCE;
    }

    @Override
    public void next(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.ORDERED);
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.CANCELLED);
    }

    @Override
    public void update() throws InvalidStateTransitionException {
        // not throw3
    }
}