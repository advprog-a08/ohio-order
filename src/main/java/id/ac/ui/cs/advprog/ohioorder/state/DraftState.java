package id.ac.ui.cs.advprog.ohioorder.state;

public class DraftState implements CheckoutState {
    @Override
    public void checkout() throws Exception {
        System.out.println("Draft checkout");
    }
}
