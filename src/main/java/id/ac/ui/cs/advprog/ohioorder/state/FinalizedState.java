package id.ac.ui.cs.advprog.ohioorder.state;

public class FinalizedState implements CheckoutState {
    @Override
    public void checkout() throws Exception {
        System.out.println("Finalized");
    }
}
