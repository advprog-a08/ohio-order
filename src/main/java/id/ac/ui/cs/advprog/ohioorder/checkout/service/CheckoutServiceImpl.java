package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.repository.CheckoutRepository;
import org.springframework.stereotype.Service;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;

    public CheckoutServiceImpl(CheckoutRepository checkoutRepository) {
        this.checkoutRepository = checkoutRepository;
    }

    public Checkout create() {
        Checkout checkout = new Checkout();
        return checkoutRepository.save(checkout);
    }
}
