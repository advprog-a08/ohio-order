package id.ac.ui.cs.advprog.ohioorder.pesanan.model;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.pesanan.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;
    private OrderItem orderItem1;
    private OrderItem orderItem2;
    private Meja meja;

    @BeforeEach
    void setUp() {
        meja = Meja.builder()
                .id(UUID.randomUUID())
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        order = Order.builder()
                .id("order-123")
                .userId("user-123")
                .meja(meja)
                .status(OrderStatus.PENDING)
                .orderItems(new ArrayList<>())
                .build();

        orderItem1 = OrderItem.builder()
                .id("item-1")
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .build();

        orderItem2 = OrderItem.builder()
                .id("item-2")
                .menuItemId("menu-2")
                .menuItemName("Pizza")
                .price(75000.0)
                .quantity(1)
                .build();
    }

    @Test
    void onCreate_SetsDefaultValues() {
        // Arrange
        Order newOrder = new Order();

        // Act
        newOrder.onCreate();

        // Assert
        assertNotNull(newOrder.getCreatedAt());
        assertNotNull(newOrder.getUpdatedAt());
        assertEquals(OrderStatus.PENDING, newOrder.getStatus());
    }

    @Test
    void onUpdate_UpdatesTimestamp() {
        // Arrange
        LocalDateTime initialUpdatedAt = order.getUpdatedAt();

        // Act
        order.onUpdate();

        // Assert
        assertNotNull(order.getUpdatedAt());
        if (initialUpdatedAt != null) {
            assertNotEquals(initialUpdatedAt, order.getUpdatedAt());
        }
    }

    @Test
    void calculateTotal_ReturnsCorrectSum() {
        // Arrange
        order.addOrderItem(orderItem1); // 50000 * 2 = 100000
        order.addOrderItem(orderItem2); // 75000 * 1 = 75000

        // Act
        double total = order.calculateTotal();

        // Assert
        assertEquals(175000.0, total);
    }

    @Test
    void calculateTotal_WithNoItems_ReturnsZero() {
        // Act
        double total = order.calculateTotal();

        // Assert
        assertEquals(0.0, total);
    }

    @Test
    void addOrderItem_AddsItemAndSetsRelationship() {
        // Act
        order.addOrderItem(orderItem1);

        // Assert
        assertTrue(order.getOrderItems().contains(orderItem1));
        assertEquals(order, orderItem1.getOrder());
    }

    @Test
    void removeOrderItem_RemovesItemAndClearsRelationship() {
        // Arrange
        order.addOrderItem(orderItem1);

        // Act
        order.removeOrderItem(orderItem1);

        // Assert
        assertFalse(order.getOrderItems().contains(orderItem1));
        assertNull(orderItem1.getOrder());
    }

    @Test
    void builderPattern_CreatesOrderCorrectly() {
        // Arrange & Act
        Order builtOrder = Order.builder()
                .id("order-test")
                .userId("user-test")
                .meja(meja)
                .status(OrderStatus.PENDING)
                .build();

        // Assert
        assertEquals("order-test", builtOrder.getId());
        assertEquals("user-test", builtOrder.getUserId());
        assertEquals(meja, builtOrder.getMeja());
        assertEquals(OrderStatus.PENDING, builtOrder.getStatus());
    }
}