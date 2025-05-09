package id.ac.ui.cs.advprog.ohioorder.checkout.controller;

import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutCreateRequest;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> cancel(@PathVariable String checkoutId) {
        return checkoutService.findById(checkoutId)
                .map(checkout -> {
                    try {
                        checkout.cancel();
                        checkoutService.save(checkout);
                    } catch (InvalidStateTransitionException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }

                    return ResponseEntity.ok(checkout);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
