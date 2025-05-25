package id.ac.ui.cs.advprog.ohioorder.pesanan.controller;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.service.OrderServiceImpl;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> createOrder(@Valid @RequestBody OrderDto.OrderRequest orderRequest) {
        CompletableFuture<OrderDto.OrderResponse> response = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/table/{MejaId}")
    public ResponseEntity<List<CompletableFuture<OrderDto.OrderResponse>>> getOrdersByMejaId(@PathVariable String MejaId) {
        List<CompletableFuture<OrderDto.OrderResponse>> responses = orderService.getOrdersByMejaId(UUID.fromString(MejaId));
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> getOrderById(@PathVariable UUID orderId) {
        CompletableFuture<OrderDto.OrderResponse> response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> addItemToOrder(
            @PathVariable UUID orderId,
            @Valid @RequestBody OrderDto.OrderItemRequest itemRequest) {
        CompletableFuture<OrderDto.OrderResponse> response = orderService.addItemToOrder(orderId, itemRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<CompletableFuture<OrderDto.OrderResponse>> updateOrderItem(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId,
            @Valid @RequestBody OrderDto.UpdateOrderItemRequest updateRequest) {
        CompletableFuture<OrderDto.OrderResponse> response = orderService.updateOrderItem(orderId, itemId, updateRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto.OrderResponse> removeItemFromOrder(
            @PathVariable UUID orderId,
            @PathVariable UUID itemId) {
        OrderDto.OrderResponse response = orderService.removeItemFromOrder(orderId, itemId);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
