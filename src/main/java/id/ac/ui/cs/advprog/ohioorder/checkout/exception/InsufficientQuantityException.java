package id.ac.ui.cs.advprog.ohioorder.checkout.exception;

public class InsufficientQuantityException extends RuntimeException {
    public InsufficientQuantityException(String message) {
        super(message);
    }
}