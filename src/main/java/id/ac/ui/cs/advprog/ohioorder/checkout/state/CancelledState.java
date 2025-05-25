package id.ac.ui.cs.advprog.ohioorder.checkout.state;

public class CancelledState extends CheckoutState {
    private static final CancelledState INSTANCE = new CancelledState();
    private CancelledState() {}

    public static CancelledState getInstance() {
        return INSTANCE;
    }

    @Override
    public String message() {
        return "Your order has been canceled. No worries — we hope to serve you next time!";
    }
}
