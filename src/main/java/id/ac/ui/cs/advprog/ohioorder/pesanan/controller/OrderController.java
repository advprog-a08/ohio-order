package id.ac.ui.cs.advprog.ohioorder.pesanan.controller;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.service.OrderServiceImpl;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderServiceImpl orderService;

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> createOrder(@Valid @RequestBody OrderDto.OrderRequest orderRequest) {
        return null;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByUserId(@PathVariable String userId) {
        return null;
    }

    @GetMapping("/table/{MejaId}")
    public ResponseEntity<List<OrderDto.OrderResponse>> getOrdersByMejaId(@PathVariable String MejaId) {
        return null;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto.OrderResponse> getOrderById(@PathVariable String orderId) {
        return null;
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<OrderDto.OrderResponse> addItemToOrder(
            @PathVariable String orderId,
            @Valid @RequestBody OrderDto.OrderItemRequest itemRequest) {
        return null;
    }

    @PutMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto.OrderResponse> updateOrderItem(
            @PathVariable String orderId,
            @PathVariable String itemId,
            @Valid @RequestBody OrderDto.UpdateOrderItemRequest updateRequest) {
        return null;
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<OrderDto.OrderResponse> removeItemFromOrder(
            @PathVariable String orderId,
            @PathVariable String itemId) {
        return null;
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDto.OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody OrderDto.UpdateOrderRequest updateRequest) {
        return null;
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) {
        return null;
    }
}
