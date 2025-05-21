package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final OrderRepository orderRepository;

    public CheckoutServiceImpl(CheckoutRepository checkoutRepository, OrderRepository orderRepository) {
        this.checkoutRepository = checkoutRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Checkout save(Checkout checkout) {
        return checkoutRepository.save(checkout);
    }

    public Optional<Checkout> create(UUID orderId) {
        Checkout checkout = new Checkout();

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            checkout.setOrder(order.get());
            return Optional.of(checkoutRepository.save(checkout));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Checkout> findById(String id) {
        return checkoutRepository.findById(UUID.fromString(id));
    }

    @Override
    public void updateById(String id) {
        findById(id).ifPresent(checkout -> {
           checkout.update();
        });
    }
}
