package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface OrderService {
    CompletableFuture<OrderDto.OrderResponse> createOrder(OrderDto.OrderRequest orderRequest, UUID tableId);
    CompletableFuture<OrderDto.OrderResponse> enrichOrderResponseAsync(OrderDto.OrderResponse orderResponse);
    List<CompletableFuture<OrderDto.OrderResponse>> getOrdersByMejaId(UUID mejaId);
    CompletableFuture<OrderDto.OrderResponse> updateOrder(UUID orderId, OrderDto.OrderRequest orderRequest);
    OrderDto.OrderResponse removeItemFromOrder(UUID orderId, UUID itemId);
    void deleteOrder(UUID orderId);
}