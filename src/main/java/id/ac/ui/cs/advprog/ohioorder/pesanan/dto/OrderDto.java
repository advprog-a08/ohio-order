package id.ac.ui.cs.advprog.ohioorder.pesanan.dto;

import id.ac.ui.cs.advprog.ohioorder.pesanan.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDto {

    public static class OrderRequest {
        private UUID mejaId;

        private String userId;

        private List<OrderItemRequest> items;
    }

    public static class OrderItemRequest {
        private String menuItemId;

        private String menuItemName;

        private Double price;

        private Integer quantity;
    }

    public static class OrderResponse {
        private String id;
        private UUID mejaId;
        private String nomorMeja;
        private String userId;
        private List<OrderItemResponse> items;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private OrderStatus status;
        private double total;
    }

    public static class OrderItemResponse {
        private String id;
        private String menuItemId;
        private String menuItemName;
        private Double price;
        private Integer quantity;
        private Double subtotal;
    }


    public static class UpdateOrderRequest {
        private OrderStatus status;
    }

    public static class UpdateOrderItemRequest {
        private Integer quantity;
    }
}
