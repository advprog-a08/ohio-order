package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderMapper;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final MejaService mejaService;

    public OrderDto.OrderResponse createOrder(OrderDto.OrderRequest orderRequest) {
        return;
    }

    public List<OrderDto.OrderResponse> getOrdersByUserId(String userId) {
        return;
    }

    public List<OrderDto.OrderResponse> getOrdersByMejaId(UUID mejaId) {
        return;
    }

    public OrderDto.OrderResponse getOrderById(String orderId) {
        return;
    }

    public OrderDto.OrderResponse addItemToOrder(String orderId, OrderDto.OrderItemRequest itemRequest) {
        return;
    }

    public OrderDto.OrderResponse updateOrderItem(String orderId, String itemId, OrderDto.UpdateOrderItemRequest updateRequest) {
        return;
    }

    public OrderDto.OrderResponse removeItemFromOrder(String orderId, String itemId) {
        return;
    }

    public OrderDto.OrderResponse updateOrderStatus(String orderId, OrderDto.UpdateOrderRequest updateRequest) {
        return;
    }

    public void deleteOrder(String orderId) {
        return;
    }
}
