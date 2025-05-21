package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto.OrderResponse createOrder(OrderDto.OrderRequest orderRequest);
    List<OrderDto.OrderResponse> getOrdersByMejaId(UUID mejaId);
    OrderDto.OrderResponse getOrderById(UUID orderId);
    OrderDto.OrderResponse addItemToOrder(UUID orderId, OrderDto.OrderItemRequest itemRequest);
    OrderDto.OrderResponse updateOrderItem(UUID orderId, UUID itemId, OrderDto.UpdateOrderItemRequest updateRequest);
    OrderDto.OrderResponse removeItemFromOrder(UUID orderId, UUID itemId);
    void deleteOrder(UUID orderId);
}
