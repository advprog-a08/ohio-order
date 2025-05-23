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

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
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
    public OrderDto.OrderResponse createOrder(OrderDto.OrderRequest orderRequest) {
    var mejaResponse = mejaService.getMejaById(orderRequest.getMejaId());

    if (!mejaResponse.getStatus().equals(MejaStatus.TERSEDIA)) {
        throw new IllegalStateException("Table is not available for ordering");
    }

    if (orderRequest.getItems() != null) {
        for (OrderDto.OrderItemRequest itemRequest : orderRequest.getItems()) {
            MenuServiceResponse menuResponse = menuServiceClient.getMenuItem(itemRequest.getMenuItemId());
            
            if (menuResponse.getData().getQuantity() < itemRequest.getQuantity()) {
                throw new IllegalStateException("Insufficient quantity available for menu item: " + 
                    itemRequest.getMenuItemId() + ". Available: " + menuResponse.getData().getQuantity() + 
                    ", Requested: " + itemRequest.getQuantity());
            }
        }
    }

    Order order = orderMapper.toEntity(orderRequest);
    Order savedOrder = orderRepository.save(order);

    return enrichOrderResponse(orderMapper.toDto(savedOrder));
}

    OrderDto.OrderResponse enrichOrderResponse(OrderDto.OrderResponse orderResponse) {
        if (orderResponse.getItems() != null) {
            double total = 0.0;

            for (OrderDto.OrderItemResponse itemResponse : orderResponse.getItems()) {
                try {
                    MenuServiceResponse menuItem = menuServiceClient.getMenuItem(itemResponse.getMenuItemId());
                    itemResponse.setMenuItemName(menuItem.getData().getName());
                    itemResponse.setMenuItemDescription(menuItem.getData().getDescription());

                    itemResponse.setPrice(menuItem.getData().getPrice());

                    double subtotal = menuItem.getData().getPrice() * itemResponse.getQuantity();
                    itemResponse.setSubtotal(subtotal);

                    total += subtotal;
                } catch (Exception e) {
                    log.warn("Menu item {} not found (possibly deleted). Using fallback values.",
                            itemResponse.getMenuItemId());

                    itemResponse.setMenuItemName("[Unavailable Item]");
                    itemResponse.setMenuItemDescription("This menu item is Unavailable.");
                    itemResponse.setMenuItemCategory("Unavailable");

                    if (itemResponse.getPrice() == null) {
                        itemResponse.setPrice(0.0);
                    }

                    double subtotal = itemResponse.getPrice() * itemResponse.getQuantity();
                    itemResponse.setSubtotal(subtotal);
                    total += subtotal;

                }
            }

            orderResponse.setTotal(total);
        }
        return orderResponse;
    }

    public List<OrderDto.OrderResponse> getOrdersByMejaId(UUID mejaId) {
        List<Order> orders = orderRepository.findByMejaId(mejaId);
        return orders.stream()
                .map(orderMapper::toDto)
                .map(this::enrichOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderDto.OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));
        return enrichOrderResponse(orderMapper.toDto(order));
    }

    @Transactional
    public OrderDto.OrderResponse addItemToOrder(UUID orderId, OrderDto.OrderItemRequest itemRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        // Check if menu item exists
        MenuServiceResponse menuResponse = menuServiceClient.getMenuItem(itemRequest.getMenuItemId());
        int availableQuantity = menuResponse.getData().getQuantity();
        
        OrderItem existingItem = orderItemRepository
                .findByOrderIdAndMenuItemId(orderId, itemRequest.getMenuItemId())
                .orElse(null);
        
        int requestedQuantity = itemRequest.getQuantity();
        
        if (existingItem != null) {
            // For existing items, check if additional quantity is available
            if (availableQuantity < requestedQuantity) {
                throw new IllegalStateException("Insufficient quantity available for menu item: " + 
                    itemRequest.getMenuItemId() + ". Available: " + availableQuantity + 
                    ", Requested: " + requestedQuantity);
            }
            
            existingItem.setQuantity(existingItem.getQuantity() + itemRequest.getQuantity());
            orderItemRepository.save(existingItem);
        } else {
            // For new items, check if quantity is available
            if (availableQuantity < requestedQuantity) {
                throw new IllegalStateException("Insufficient quantity available for menu item: " + 
                    itemRequest.getMenuItemId() + ". Available: " + availableQuantity + 
                    ", Requested: " + requestedQuantity);
            }
            
            OrderItem newItem = OrderItem.builder()
                    .menuItemId(itemRequest.getMenuItemId())
                    .quantity(itemRequest.getQuantity())
                    .build();
            order.addOrderItem(newItem);
        }

        Order updatedOrder = orderRepository.save(order);
        return enrichOrderResponse(orderMapper.toDto(updatedOrder));
    }
    
    @Transactional
    public OrderDto.OrderResponse updateOrderItem(UUID orderId, UUID itemId, OrderDto.UpdateOrderItemRequest updateRequest) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

        OrderItem itemToUpdate = order.getOrderItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Order item not found with ID: " + itemId));
                
        String menuItemId = itemToUpdate.getMenuItemId();
        int currentQuantity = itemToUpdate.getQuantity();
        int newQuantity = updateRequest.getQuantity();
        
        // Only check availability if increasing quantity
        if (newQuantity > currentQuantity) {
            MenuServiceResponse menuResponse = menuServiceClient.getMenuItem(menuItemId);
            int availableQuantity = menuResponse.getData().getQuantity();
            int additionalQuantity = newQuantity - currentQuantity;
            
            if (availableQuantity < additionalQuantity) {
                throw new IllegalStateException("Insufficient quantity available for menu item: " + 
                    menuItemId + ". Available: " + availableQuantity + 
                    ", Additional requested: " + additionalQuantity);
            }
        }

        itemToUpdate.setQuantity(newQuantity);
        Order updatedOrder = orderRepository.save(order);

        return enrichOrderResponse(orderMapper.toDto(updatedOrder));
    }

    @Transactional
    public OrderDto.OrderResponse removeItemFromOrder(UUID orderId, UUID itemId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with ID: " + orderId));

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

        orderRepository.delete(order);
    }
}