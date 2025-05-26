package id.ac.ui.cs.advprog.ohioorder.checkout.controller;

import id.ac.ui.cs.advprog.ohioorder.annotation.RequireAdmin;
import id.ac.ui.cs.advprog.ohioorder.annotation.RequireTableSession;
import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutCreateRequest;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InsufficientQuantityException;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping
    @RequireAdmin
    public ResponseEntity<List<Checkout>> findAll() {
        return ResponseEntity.ok(checkoutService.findAll());
    }

    @GetMapping("{checkoutId}")
    @RequireAdmin
    @RequireTableSession
    public ResponseEntity<?> findById(@PathVariable String checkoutId) {
        return checkoutService.findById(checkoutId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @RequireTableSession
    public ResponseEntity<Checkout> create(@RequestBody CheckoutCreateRequest request) {
        return checkoutService.create(request.getOrderId())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PostMapping("{checkoutId}/advance")
    @RequireAdmin
    public ResponseEntity<?> advance(@PathVariable String checkoutId) {
        return checkoutService.findById(checkoutId)
                .map(checkout -> {
                    try {
                        checkout.advance();
                        checkoutService.save(checkout);
                    } catch (InvalidStateTransitionException | InsufficientQuantityException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }

                    return ResponseEntity.ok(checkout);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{checkoutId}")
    @RequireTableSession
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
