package id.ac.ui.cs.advprog.ohioorder.checkout.controller;

import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutCreateRequest;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("create")
    public ResponseEntity<Checkout> create(@RequestBody CheckoutCreateRequest request) {
        return checkoutService.create(request.getOrderId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("cancel/{checkoutId}")
    public ResponseEntity<Checkout> cancel(@PathVariable String checkoutId) {
        Optional<Checkout> checkout = checkoutService.findById(checkoutId);

        if (checkout.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Checkout checkout1 = checkout.get();
        checkout1.cancel();
        checkoutService.save(checkout1);

        return ResponseEntity.ok(checkout1);
    }
}
