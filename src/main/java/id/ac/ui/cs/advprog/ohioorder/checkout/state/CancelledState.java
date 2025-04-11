package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import org.springframework.stereotype.Component;

@Component
public class CancelledState implements CheckoutState {
    @Override
    public void next(Checkout checkout) {
    }

    @Override
    public void cancel(Checkout checkout) {
    }
}
