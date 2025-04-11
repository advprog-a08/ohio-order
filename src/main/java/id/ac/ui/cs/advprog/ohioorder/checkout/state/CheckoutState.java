package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

public interface CheckoutState {
    void next(Checkout checkout);
    void cancel(Checkout checkout);
}
