package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

import java.util.Optional;

public interface CheckoutService {
    Checkout create();
    Optional<Checkout> findById(String id);
}
