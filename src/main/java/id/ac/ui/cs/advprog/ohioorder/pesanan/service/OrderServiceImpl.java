package id.ac.ui.cs.advprog.ohioorder.pesanan.service;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuServiceResponse;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderMapper;
import id.ac.ui.cs.advprog.ohioorder.pesanan.client.MenuServiceClient;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderItemRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final MejaService mejaService;
    private final MenuServiceClient menuServiceClient;

    @Transactional
    public CompletableFuture<OrderDto.OrderResponse> createOrder(OrderDto.OrderRequest orderRequest, UUID tableId) {
        if (orderRequest.getItems() != null) {
            for (OrderDto.OrderItemRequest itemRequest : orderRequest.getItems()) {
                menuServiceClient.getMenuItem(itemRequest.getMenuItemId());
            }
        }

        if (orderRequest.getItems() != null && !orderRequest.getItems().isEmpty()) {
            Set<String> uniqueMenuItems = new HashSet<>();
            List<String> duplicateItems = new ArrayList<>();

            for (OrderDto.OrderItemRequest itemRequest : orderRequest.getItems()) {
                String menuItemId = itemRequest.getMenuItemId();
                if (!uniqueMenuItems.add(menuItemId)) {
                    duplicateItems.add(menuItemId);
                }

                menuServiceClient.getMenuItem(menuItemId);
            }

            if (!duplicateItems.isEmpty()) {
                throw new IllegalArgumentException("Duplicate menu items found: " + String.join(", ", duplicateItems));
            }
        }

        OrderDto.OrderRequest newOrderRequest = OrderDto.OrderRequest.builder()
                .items(orderRequest.getItems())
                .mejaId(tableId)
                .locked(false)
                .build();

        Order order = orderMapper.toEntity(newOrderRequest);
        Order savedOrder = orderRepository.save(order);

        return enrichOrderResponseAsync(orderMapper.toDto(savedOrder));
    }

    public CompletableFuture<OrderDto.OrderResponse> enrichOrderResponseAsync(OrderDto.OrderResponse orderResponse) {
        if (orderResponse.getItems() == null || orderResponse.getItems().isEmpty()) {
            return CompletableFuture.completedFuture(orderResponse);
        }

        List<String> menuItemIds = orderResponse.getItems().stream()
                .map(OrderDto.OrderItemResponse::getMenuItemId)
                .collect(Collectors.toList());

        return menuServiceClient.getMultipleMenuItemsAsync(menuItemIds)
                .thenApply(menuResponses -> {
                    Map<String, MenuServiceResponse> menuItemMap = menuResponses.stream()
                            .collect(Collectors.toMap(
                                    response -> response.getData().getId(),
                                    response -> response,
                                    (r1, r2) -> r1
                            ));

                    double total = 0.0;

                    for (OrderDto.OrderItemResponse itemResponse : orderResponse.getItems()) {
                        try {
                            MenuServiceResponse menuResponse = menuItemMap.get(itemResponse.getMenuItemId());
                            if (menuResponse != null) {
                                itemResponse.setMenuItemName(menuResponse.getData().getName());
                                itemResponse.setMenuItemDescription(menuResponse.getData().getDescription());
                                itemResponse.setPrice(menuResponse.getData().getPrice());

                                double subtotal = menuResponse.getData().getPrice() * itemResponse.getQuantity();
                                itemResponse.setSubtotal(subtotal);
                                total += subtotal;
                            } else {
                                handleUnavailableItem(itemResponse);
                                total += itemResponse.getSubtotal();
                            }
                        } catch (Exception e) {
                            log.warn("Menu item {} not found. Using fallback values.", itemResponse.getMenuItemId());
                            handleUnavailableItem(itemResponse);
                            total += itemResponse.getSubtotal();
                        }
                    }

                    orderResponse.setTotal(total);
                    return orderResponse;
                });
    }

    private void handleUnavailableItem(OrderDto.OrderItemResponse itemResponse) {
        itemResponse.setMenuItemName("[Unavailable Item]");
        itemResponse.setMenuItemDescription("This menu item is Unavailable.");
        itemResponse.setMenuItemCategory("Unavailable");

        if (itemResponse.getPrice() == null) {
            itemResponse.setPrice(0.0);
        }

        double subtotal = itemResponse.getPrice() * itemResponse.getQuantity();
        itemResponse.setSubtotal(subtotal);
    }

    public List<CompletableFuture<OrderDto.OrderResponse>> getOrdersByMejaId(UUID mejaId) {
        List<Order> orders = orderRepository.findByMejaId(mejaId);
        return orders.stream()
                .map(orderMapper::toDto)
                .map(this::enrichOrderResponseAsync)
                .collect(Collectors.toList());
    }

    public CompletableFuture<OrderDto.OrderResponse> getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
        return enrichOrderResponseAsync(orderMapper.toDto(order));
    }

    @Transactional
    public CompletableFuture<OrderDto.OrderResponse> addItemToOrder(UUID orderId, OrderDto.OrderItemRequest itemRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getLocked()) {
            throw new IllegalStateException("Cannot modify order because it has been checked out");
        }

        menuServiceClient.getMenuItem(itemRequest.getMenuItemId());

        OrderItem existingItem = orderItemRepository
                .findByOrderIdAndMenuItemId(orderId, itemRequest.getMenuItemId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + itemRequest.getQuantity());
            orderItemRepository.save(existingItem);
        } else {
            OrderItem newItem = OrderItem.builder()
                    .menuItemId(itemRequest.getMenuItemId())
                    .quantity(itemRequest.getQuantity())
                    .build();
            order.addOrderItem(newItem);
        }

        Order updatedOrder = orderRepository.save(order);
        return enrichOrderResponseAsync(orderMapper.toDto(updatedOrder));
    }

    @Transactional
    public CompletableFuture<OrderDto.OrderResponse> updateOrderItem(UUID orderId, UUID itemId, OrderDto.UpdateOrderItemRequest updateRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getLocked()) {
            throw new IllegalStateException("Cannot modify order because it has been checked out");
        }

        OrderItem itemToUpdate = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Order item not found with ID: " + itemId));

        itemToUpdate.setQuantity(updateRequest.getQuantity());
        Order updatedOrder = orderRepository.save(order);

        return enrichOrderResponseAsync(orderMapper.toDto(updatedOrder));
    }

    @Transactional
    public OrderDto.OrderResponse removeItemFromOrder(UUID orderId, UUID itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getLocked()) {
            throw new IllegalStateException("Cannot modify order because it has been checked out");
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
    public void deleteOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        if (order.getLocked()) {
            throw new IllegalStateException("Cannot delete order because it has been checked out");
        }

        orderRepository.delete(order);
    }
}