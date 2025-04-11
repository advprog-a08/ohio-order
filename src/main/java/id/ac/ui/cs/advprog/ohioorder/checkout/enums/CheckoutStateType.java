package id.ac.ui.cs.advprog.ohioorder.checkout.enums;

import id.ac.ui.cs.advprog.ohioorder.checkout.state.*;

public enum CheckoutStateType {
    DRAFT(DraftState.getInstance()),
    ORDERED(OrderedState.getInstance()),
    PREPARING(PreparingState.getInstance()),
    READY(ReadyState.getInstance()),
    COMPLETED(CompletedState.getInstance()),
    CANCELLED(CancelledState.getInstance());

    private final CheckoutState state;

    CheckoutStateType(CheckoutState state) {
        this.state = state;
    }

    public CheckoutState getCheckoutState() {
        return state;
    }
}