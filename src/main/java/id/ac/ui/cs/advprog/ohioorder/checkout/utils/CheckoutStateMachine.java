package id.ac.ui.cs.advprog.ohioorder.checkout.utils;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CancelledState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CheckoutState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CompletedState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.DraftState;

public class CheckoutStateMachine {
    private CheckoutStateMachine() {}

    public static CheckoutState getStateForStatus(CheckoutStateType status) {
        return switch (status) {
            case DRAFT -> DraftState.getInstance();
            case COMPLETED -> CompletedState.getInstance();
            case CANCELLED -> CancelledState.getInstance();
        };
    }
}
