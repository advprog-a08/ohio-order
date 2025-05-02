package id.ac.ui.cs.advprog.ohioorder.checkout.exception;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
