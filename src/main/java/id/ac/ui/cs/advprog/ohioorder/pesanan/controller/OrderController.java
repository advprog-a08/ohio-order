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

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> createOrder(@Valid @RequestBody OrderDto.OrderRequest orderRequest) {
        OrderDto.OrderResponse response = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByUserId(@PathVariable String userId) {
        List<OrderDto.OrderResponse> responses = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/table/{MejaId}")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByMejaId(@PathVariable String MejaId) {
        List<OrderDto.OrderResponse> responses = orderService.getOrdersByMejaId(UUID.fromString(MejaId));
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto.OrderResponse> getOrderById(@PathVariable String orderId) {
        OrderDto.OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDto.OrderResponse> addItemToOrder(
            @PathVariable String orderId,
            @Valid @RequestBody OrderDto.OrderItemRequest itemRequest) {
        OrderDto.OrderResponse response = orderService.addItemToOrder(orderId, itemRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto.OrderResponse> updateOrderItem(
            @PathVariable String orderId,
            @PathVariable String itemId,
            @Valid @RequestBody OrderDto.UpdateOrderItemRequest updateRequest) {
        OrderDto.OrderResponse response = orderService.updateOrderItem(orderId, itemId, updateRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto.OrderResponse> removeItemFromOrder(
            @PathVariable String orderId,
            @PathVariable String itemId) {
        OrderDto.OrderResponse response = orderService.removeItemFromOrder(orderId, itemId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto.OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody OrderDto.UpdateOrderRequest updateRequest) {
        OrderDto.OrderResponse response = orderService.updateOrderStatus(orderId, updateRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
