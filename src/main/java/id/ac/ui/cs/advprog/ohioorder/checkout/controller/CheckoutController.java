package id.ac.ui.cs.advprog.ohioorder.checkout.controller;

import id.ac.ui.cs.advprog.ohioorder.annotation.AuthenticatedTableSession;
import id.ac.ui.cs.advprog.ohioorder.annotation.RequireAdmin;
import id.ac.ui.cs.advprog.ohioorder.annotation.RequireTableSession;
import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutCreateRequest;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InsufficientQuantityException;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import id.ac.ui.cs.advprog.ohioorder.grpc.TableSessionGrpcClient;
import id.ac.ui.cs.advprog.ohioorder.model.TableSession;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/checkout")
@AllArgsConstructor
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final TableSessionGrpcClient tableSessionGrpcClient;

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
    public ResponseEntity<Checkout> create(@AuthenticatedTableSession TableSession tableSession) {
        Optional<Checkout> checkout = checkoutService.create(UUID.fromString(tableSession.getOrderId()));

        if (checkout.isPresent()) {
            tableSessionGrpcClient.setCheckoutIdToTableSession(tableSession.getId(), checkout.get().getId().toString());
            return ResponseEntity.ok(checkout.get());
        }

        return ResponseEntity.badRequest().build();
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

    @GetMapping("me")
    @RequireTableSession
    public ResponseEntity<?> getMe(@AuthenticatedTableSession TableSession tableSession) {
        if (tableSession.getCheckoutId().isPresent()) {
            return checkoutService.findById(tableSession.getCheckoutId().get())
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("me")
    @RequireTableSession
    public ResponseEntity<?> cancelMe(@AuthenticatedTableSession TableSession tableSession) {
        if (tableSession.getCheckoutId().isPresent()) {
            return checkoutService.findById(tableSession.getCheckoutId().get())
                    .map(checkout -> {
                        try {
                            checkout.cancel();
                            checkoutService.save(checkout);

                            tableSessionGrpcClient.unsetCheckoutIdToTableSession(tableSession.getId());
                        } catch (InvalidStateTransitionException e) {
                            return ResponseEntity.badRequest().body(e.getMessage());
                        }

                        return ResponseEntity.ok(checkout);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        return ResponseEntity.badRequest().build();
    }
}
