package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderDto.OrderResponse createOrder(OrderDto.OrderRequest orderRequest);
    List<OrderDto.OrderResponse> getOrdersByUserId(String userId);
    List<OrderDto.OrderResponse> getOrdersByMejaId(UUID mejaId);
    OrderDto.OrderResponse getOrderById(String orderId);
    OrderDto.OrderResponse addItemToOrder(String orderId, OrderDto.OrderItemRequest itemRequest);
    OrderDto.OrderResponse updateOrderItem(String orderId, String itemId, OrderDto.UpdateOrderItemRequest updateRequest);
    OrderDto.OrderResponse removeItemFromOrder(String orderId, String itemId);
    OrderDto.OrderResponse updateOrderStatus(String orderId, OrderDto.UpdateOrderRequest updateRequest);
    void deleteOrder(String orderId);
}
