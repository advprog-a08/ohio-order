package id.ac.ui.cs.advprog.ohioorder.checkout.state;

public class CompletedState extends CheckoutState {
    private static final CompletedState INSTANCE = new CompletedState();
    private CompletedState() {}

    public static CompletedState getInstance() {
        return INSTANCE;
    }

    @Override
    public String message() {
        return "Enjoy your meal! Let us know if you need anything else.";
    }
}
