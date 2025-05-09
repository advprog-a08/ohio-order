package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

import java.util.Optional;

public interface CheckoutService {
    Checkout save(Checkout checkout);
    Optional<Checkout> create(String orderId);
    Optional<Checkout> findById(String id);
    void updateById(String id);
}
