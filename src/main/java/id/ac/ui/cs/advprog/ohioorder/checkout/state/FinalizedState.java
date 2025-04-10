package id.ac.ui.cs.advprog.ohioorder.checkout.state;

public class FinalizedState implements CheckoutState {
    @Override
    public void checkout() throws Exception {
        System.out.println("Finalized");
    }
}
