package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CheckoutService {
    Checkout save(Checkout checkout);
    Optional<Checkout> create(UUID orderId);
    Optional<Checkout> findById(String id);
    List<Checkout> findAll();

    List<CheckoutResponse> findAllFormatted();
}
