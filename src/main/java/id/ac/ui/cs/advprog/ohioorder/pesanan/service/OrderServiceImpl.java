package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderMapper;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import id.ac.ui.cs.advprog.ohioorder.pesanan.enums.OrderStatus;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final MejaService mejaService;

    @Transactional
    public OrderDto.OrderResponse createOrder(OrderDto.OrderRequest orderRequest) {
        var mejaResponse = mejaService.getMejaById(orderRequest.getMejaId());

        if (!mejaResponse.getStatus().equals(MejaStatus.TERSEDIA)) {
            throw new IllegalStateException("Table is not available for ordering");
        }

        List<Order> existingOrders = orderRepository.findByMejaIdAndStatus(
                orderRequest.getMejaId(),
                OrderStatus.PENDING);

        if (!existingOrders.isEmpty()) {
            throw new IllegalStateException("This table already has a pending order");
        }

        Order order = orderMapper.toEntity(orderRequest);

        // Set the meja as occupied
        mejaService.setMejaStatus(orderRequest.getMejaId(), MejaStatus.TERISI);

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }

    public List<OrderDto.OrderResponse> getOrdersByUserId(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto.OrderResponse> getOrdersByMejaId(UUID mejaId) {
        List<Order> orders = orderRepository.findByMejaId(mejaId);
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    public OrderDto.OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDto.OrderResponse addItemToOrder(String orderId, OrderDto.OrderItemRequest itemRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot add items to an order that is " + order.getStatus());
        }

        OrderItem existingItem = orderItemRepository
                .findByOrderIdAndMenuItemId(orderId, itemRequest.getMenuItemId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + itemRequest.getQuantity());
            orderItemRepository.save(existingItem);
        } else {
            OrderItem newItem = OrderItem.builder()
                    .menuItemId(itemRequest.getMenuItemId())
                    .menuItemName(itemRequest.getMenuItemName())
                    .price(itemRequest.getPrice())
                    .quantity(itemRequest.getQuantity())
                    .build();
            order.addOrderItem(newItem);
        }

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public OrderDto.OrderResponse updateOrderItem(String orderId, String itemId, OrderDto.UpdateOrderItemRequest updateRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot update items in an order that is " + order.getStatus());
        }

        OrderItem itemToUpdate = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Order item not found with ID: " + itemId));

        itemToUpdate.setQuantity(updateRequest.getQuantity());
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public OrderDto.OrderResponse removeItemFromOrder(String orderId, String itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Cannot remove items from an order that is " + order.getStatus());
        }

        OrderItem itemToRemove = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Order item not found with ID: " + itemId));

        order.removeOrderItem(itemToRemove);
        orderItemRepository.delete(itemToRemove);

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public OrderDto.OrderResponse updateOrderStatus(String orderId, OrderDto.UpdateOrderRequest updateRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (OrderStatus.DELIVERED.equals(order.getStatus()) || OrderStatus.CANCELLED.equals(order.getStatus())) {
            throw new IllegalStateException("Cannot update status of an order that is already " + order.getStatus());
        }

        OrderStatus newStatus = updateRequest.getStatus();
        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED) {
            mejaService.setMejaStatus(order.getMeja().getId(), MejaStatus.TERSEDIA);
        }

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.PENDING) {
            mejaService.setMejaStatus(order.getMeja().getId(), MejaStatus.TERSEDIA);
        }

        orderRepository.delete(order);
    }
}
