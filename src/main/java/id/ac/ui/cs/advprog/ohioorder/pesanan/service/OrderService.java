package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    CompletableFuture<OrderDto.OrderResponse> createOrder(OrderDto.OrderRequest orderRequest, UUID tableId);
    CompletableFuture<OrderDto.OrderResponse> enrichOrderResponseAsync(OrderDto.OrderResponse orderResponse);
    CompletableFuture<OrderDto.OrderResponse> getOrderById(UUID orderId);
    CompletableFuture<OrderDto.OrderResponse> updateOrder(String sessionOrderId, OrderDto.OrderRequest orderRequest);
    OrderDto.OrderResponse removeItemFromOrder(String sessionOrderId, UUID itemId);
    void deleteOrder(UUID orderId);
}