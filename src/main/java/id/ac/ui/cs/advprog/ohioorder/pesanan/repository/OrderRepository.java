package id.ac.ui.cs.advprog.ohioorder.pesanan.repository;

import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.enums.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository {
    List<Order> findByUserId(String userId);
    List<Order> findByMejaId(UUID mejaId);
    List<Order> findByMejaIdAndStatus(UUID mejaId, OrderStatus status);
}
