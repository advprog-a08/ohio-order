package id.ac.ui.cs.advprog.ohioorder.pesanan.dto;

import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    private final MejaService mejaService;

    public OrderMapper(MejaService mejaService) {
        this.mejaService = mejaService;
    }

    public Order toEntity(OrderDto.OrderRequest orderRequest) {
        // Get the Meja instance
        var mejaResponse = mejaService.getMejaById(orderRequest.getMejaId());
        Meja meja = Meja.builder()
                .id(mejaResponse.getId())
                .nomorMeja(mejaResponse.getNomorMeja())
                .status(mejaResponse.getStatus())
                .build();

        Order order = Order.builder()
                .userId(orderRequest.getUserId())
                .meja(meja)
                .build();

        if (order.getOrderItems() == null) {
            order.setOrderItems(new ArrayList<>());
        }

        if (orderRequest.getItems() != null) {
            List<OrderItem> orderItems = orderRequest.getItems().stream()
                    .map(itemRequest -> {
                        OrderItem orderItem = OrderItem.builder()
                                .menuItemId(itemRequest.getMenuItemId())
                                .menuItemName(itemRequest.getMenuItemName())
                                .price(itemRequest.getPrice())
                                .quantity(itemRequest.getQuantity())
                                .build();
                        order.addOrderItem(orderItem);
                        return orderItem;
                    })
                    .toList();
        }

        return order;
    }

    public OrderDto.OrderResponse toDto(Order order) {
        List<OrderDto.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return OrderDto.OrderResponse.builder()
                .id(order.getId())
                .mejaId(order.getMeja().getId())
                .nomorMeja(order.getMeja().getNomorMeja())
                .userId(order.getUserId())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .status(order.getStatus())
                .total(order.calculateTotal())
                .build();
    }

    public OrderDto.OrderItemResponse toDto(OrderItem orderItem) {
        return OrderDto.OrderItemResponse.builder()
                .id(orderItem.getId())
                .menuItemId(orderItem.getMenuItemId())
                .menuItemName(orderItem.getMenuItemName())
                .price(orderItem.getPrice())
                .quantity(orderItem.getQuantity())
                .subtotal(orderItem.getPrice() * orderItem.getQuantity())
                .build();
    }
}