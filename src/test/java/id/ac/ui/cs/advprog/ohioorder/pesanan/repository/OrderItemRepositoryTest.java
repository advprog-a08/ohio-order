package id.ac.ui.cs.advprog.ohioorder.pesanan.repository;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private Order order;
    private OrderItem orderItem1;
    private OrderItem orderItem2;

    @BeforeEach
    void setUp() {
        Meja meja = Meja.builder()
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        entityManager.persistAndFlush(meja);

        order = Order.builder()
                .meja(meja)
                .build();

        entityManager.persist(order);

        orderItem1 = OrderItem.builder()
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .order(order)
                .build();

        orderItem2 = OrderItem.builder()
                .menuItemId("menu-2")
                .menuItemName("Pizza")
                .price(75000.0)
                .quantity(1)
                .order(order)
                .build();

        entityManager.persist(orderItem1);
        entityManager.persist(orderItem2);

        entityManager.flush();
    }

    @Test
    void findByOrderIdAndMenuItemId_ReturnsCorrectItem() {
        Optional<OrderItem> result1 = orderItemRepository.findByOrderIdAndMenuItemId(order.getId(), "menu-1");
        Optional<OrderItem> result2 = orderItemRepository.findByOrderIdAndMenuItemId(order.getId(), "menu-2");
        Optional<OrderItem> result3 = orderItemRepository.findByOrderIdAndMenuItemId(order.getId(), "menu-3");
        Optional<OrderItem> result4 = orderItemRepository.findByOrderIdAndMenuItemId("non-existent", "menu-1");

        assertTrue(result1.isPresent());
        assertEquals(orderItem1, result1.get());

        assertTrue(result2.isPresent());
        assertEquals(orderItem2, result2.get());

        assertTrue(result3.isEmpty());
        assertTrue(result4.isEmpty());
    }

    @Test
    void findById_ReturnsCorrectItem() {
        Optional<OrderItem> result1 = orderItemRepository.findById(orderItem1.getId());
        Optional<OrderItem> result2 = orderItemRepository.findById(orderItem2.getId());
        Optional<OrderItem> result3 = orderItemRepository.findById("non-existent");

        assertTrue(result1.isPresent());
        assertEquals(orderItem1, result1.get());

        assertTrue(result2.isPresent());
        assertEquals(orderItem2, result2.get());

        assertTrue(result3.isEmpty());
    }

    @Test
    void save_CreatesNewOrderItem() {
        OrderItem newItem = OrderItem.builder()
                .menuItemId("menu-3")
                .menuItemName("Soda")
                .price(15000.0)
                .quantity(3)
                .order(order)
                .build();

        OrderItem savedItem = orderItemRepository.save(newItem);

        assertNotNull(savedItem.getId());
        assertEquals("menu-3", savedItem.getMenuItemId());
        assertEquals("Soda", savedItem.getMenuItemName());
        assertEquals(15000.0, savedItem.getPrice());
        assertEquals(3, savedItem.getQuantity());
        assertEquals(order, savedItem.getOrder());

        Optional<OrderItem> retrievedItem = orderItemRepository.findById(savedItem.getId());
        assertTrue(retrievedItem.isPresent());
        assertEquals(savedItem, retrievedItem.get());
    }

    @Test
    void delete_RemovesOrderItem() {
        orderItemRepository.delete(orderItem1);
        entityManager.flush();

        Optional<OrderItem> retrievedItem = orderItemRepository.findById(orderItem1.getId());
        assertTrue(retrievedItem.isEmpty());

        Optional<OrderItem> otherItem = orderItemRepository.findById(orderItem2.getId());
        assertTrue(otherItem.isPresent());
    }
}