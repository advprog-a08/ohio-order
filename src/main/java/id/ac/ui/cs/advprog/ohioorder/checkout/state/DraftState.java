package id.ac.ui.cs.advprog.ohioorder.checkout.state;

public class DraftState implements CheckoutState {
    @Override
    public void checkout() throws Exception {
        System.out.println("Draft checkout");
    }
}
