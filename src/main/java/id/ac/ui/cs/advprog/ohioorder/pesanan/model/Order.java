package id.ac.ui.cs.advprog.ohioorder.pesanan.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.pesanan.enums.OrderStatus;

public class Order {
    private String id;

    private String userId;

    private Meja meja;

    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private OrderStatus status;

    protected void onCreate() {
    }

    protected void onUpdate() {
    }

    public double calculateTotal() {
        return 0;
    }

    public void addOrderItem(OrderItem orderItem) {
    }

    public void removeOrderItem(OrderItem orderItem) {
    }
}