package id.ac.ui.cs.advprog.ohioorder.pesanan.repository;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.pesanan.enums.OrderStatus;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private Meja meja1;
    private Meja meja2;
    private Order order1;
    private Order order2;
    private Order order3;
    private String userId1 = "user-123";
    private String userId2 = "user-456";

    @BeforeEach
    void setUp() {
        // Create test data
        meja1 = Meja.builder()
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        meja2 = Meja.builder()
                .nomorMeja("A2")
                .status(MejaStatus.TERISI)
                .build();

        entityManager.persist(meja1);
        entityManager.persist(meja2);

        order1 = Order.builder()
                .userId(userId1)
                .meja(meja1)
                .status(OrderStatus.PENDING)
                .build();

        order2 = Order.builder()
                .userId(userId1)
                .meja(meja2)
                .status(OrderStatus.DELIVERED)
                .build();

        order3 = Order.builder()
                .userId(userId2)
                .meja(meja1)
                .status(OrderStatus.PENDING)
                .build();

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);

        entityManager.flush();
    }

    @Test
    void findByUserId_ReturnsCorrectOrders() {
        // Act
        List<Order> result1 = orderRepository.findByUserId(userId1);
        List<Order> result2 = orderRepository.findByUserId(userId2);
        List<Order> result3 = orderRepository.findByUserId("non-existent");

        // Assert
        assertEquals(2, result1.size());
        assertTrue(result1.contains(order1));
        assertTrue(result1.contains(order2));

        assertEquals(1, result2.size());
        assertTrue(result2.contains(order3));

        assertTrue(result3.isEmpty());
    }

    @Test
    void findByMejaId_ReturnsCorrectOrders() {
        // Act
        List<Order> result1 = orderRepository.findByMejaId(meja1.getId());
        List<Order> result2 = orderRepository.findByMejaId(meja2.getId());
        List<Order> result3 = orderRepository.findByMejaId(UUID.randomUUID());

        // Assert
        assertEquals(2, result1.size());
        assertTrue(result1.contains(order1));
        assertTrue(result1.contains(order3));

        assertEquals(1, result2.size());
        assertTrue(result2.contains(order2));

        assertTrue(result3.isEmpty());
    }

    @Test
    void findByMejaIdAndStatus_ReturnsCorrectOrders() {
        // Act
        List<Order> result1 = orderRepository.findByMejaIdAndStatus(meja1.getId(), OrderStatus.PENDING);
        List<Order> result2 = orderRepository.findByMejaIdAndStatus(meja1.getId(), OrderStatus.DELIVERED);
        List<Order> result3 = orderRepository.findByMejaIdAndStatus(meja2.getId(), OrderStatus.DELIVERED);

        // Assert
        assertEquals(2, result1.size());
        assertTrue(result1.contains(order1));
        assertTrue(result1.contains(order3));

        assertTrue(result2.isEmpty());

        assertEquals(1, result3.size());
        assertTrue(result3.contains(order2));
    }
}