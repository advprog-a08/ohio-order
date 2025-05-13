package id.ac.ui.cs.advprog.ohioorder.pesanan.model;

import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
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
                .meja(meja)
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
        Order newOrder = new Order();

        newOrder.onCreate();

        assertNotNull(newOrder.getCreatedAt());
        assertNotNull(newOrder.getUpdatedAt());
    }

    @Test
    void onUpdate_UpdatesTimestamp() {
        LocalDateTime initialUpdatedAt = order.getUpdatedAt();

        order.onUpdate();

        assertNotNull(order.getUpdatedAt());
        if (initialUpdatedAt != null) {
            assertNotEquals(initialUpdatedAt, order.getUpdatedAt());
        }
    }

    @Test
    void calculateTotal_ReturnsCorrectSum() {
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        double total = order.calculateTotal();

        assertEquals(175000.0, total);
    }

    @Test
    void calculateTotal_WithNoItems_ReturnsZero() {
        double total = order.calculateTotal();

        assertEquals(0.0, total);
    }

    @Test
    void addOrderItem_AddsItemAndSetsRelationship() {
        order.addOrderItem(orderItem1);

        assertTrue(order.getOrderItems().contains(orderItem1));
        assertEquals(order, orderItem1.getOrder());
    }

    @Test
    void removeOrderItem_RemovesItemAndClearsRelationship() {
        order.addOrderItem(orderItem1);

        order.removeOrderItem(orderItem1);

        assertFalse(order.getOrderItems().contains(orderItem1));
        assertNull(orderItem1.getOrder());
    }

    @Test
    void builderPattern_CreatesOrderCorrectly() {
        Order builtOrder = Order.builder()
                .id("order-test")
                .meja(meja)
                .build();

        assertEquals("order-test", builtOrder.getId());
        assertEquals(meja, builtOrder.getMeja());
    }
}