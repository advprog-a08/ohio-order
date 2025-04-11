package id.ac.ui.cs.advprog.ohioorder.pesanan.repository;

import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository {
    Optional<OrderItem> findByOrderIdAndMenuItemId(String orderId, String menuItemId);
}
