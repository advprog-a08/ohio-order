package id.ac.ui.cs.advprog.ohioorder.checkout.repository;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, UUID> {
    @Query("SELECT DISTINCT c FROM Checkout c JOIN FETCH c.order")
    List<Checkout> findAllComplete();

    @Query("SELECT c FROM Checkout c ORDER BY c.order.createdAt")
    List<Checkout> findAllOrderByOrderCreatedAt();
}
