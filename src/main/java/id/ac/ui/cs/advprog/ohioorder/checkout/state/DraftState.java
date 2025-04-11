package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public class DraftState implements CheckoutState {
    private static DraftState INSTANCE;
    private DraftState() {}

    public static DraftState getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DraftState();
        }

        return INSTANCE;
    }

    @Override
    public void next(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.COMPLETED);
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.CANCELLED);
    }
}