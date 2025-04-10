package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import org.springframework.stereotype.Component;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

@Component
public class CompletedState implements CheckoutState {
    @Override
    public void next(Checkout checkout) {
    }
}
