package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    CompletableFuture<OrderDto.OrderResponse> createOrder(OrderDto.OrderRequest orderRequest);
    List<CompletableFuture<OrderDto.OrderResponse>> getOrdersByMejaId(UUID mejaId);
    CompletableFuture<OrderDto.OrderResponse> getOrderById(UUID orderId);
    CompletableFuture<OrderDto.OrderResponse> addItemToOrder(UUID orderId, OrderDto.OrderItemRequest itemRequest);
    CompletableFuture<OrderDto.OrderResponse> updateOrderItem(UUID orderId, UUID itemId, OrderDto.UpdateOrderItemRequest updateRequest);
    OrderDto.OrderResponse removeItemFromOrder(UUID orderId, UUID itemId);
    void deleteOrder(UUID orderId);
}
