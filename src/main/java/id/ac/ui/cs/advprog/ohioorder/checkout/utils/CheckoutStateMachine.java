package id.ac.ui.cs.advprog.ohioorder.checkout.utils;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.*;

public class CheckoutStateMachine {
    private CheckoutStateMachine() {}

    public static CheckoutState getStateForStatus(CheckoutStateType status) {
        return switch (status) {
            case DRAFT -> DraftState.getInstance();
            case ORDERED -> OrderedState.getInstance();
            case PREPARING -> PreparingState.getInstance();
            case READY -> ReadyState.getInstance();
            case COMPLETED -> CompletedState.getInstance();
            case CANCELLED -> CancelledState.getInstance();
        };
    }
}
