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

    private UUID orderId;
    private Order order;
    private UUID orderItem1Id;
    private OrderItem orderItem1;
    private UUID orderItem2Id;
    private OrderItem orderItem2;
    private Meja meja;

    @BeforeEach
    void setUp() {
        meja = Meja.builder()
                .id(UUID.randomUUID())
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        orderId = UUID.randomUUID();
        order = Order.builder()
                .id(orderId)
                .meja(meja)
                .orderItems(new ArrayList<>())
                .build();

        orderItem1Id = UUID.randomUUID();
        orderItem1 = OrderItem.builder()
                .id(orderItem1Id)
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .build();

        orderItem2Id = UUID.randomUUID();
        orderItem2 = OrderItem.builder()
                .id(orderItem2Id)
                .menuItemId("menu-2")
                .menuItemName("Pizza")
                .price(75000.0)
                .quantity(1)
                .build();
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
        UUID orderId = UUID.randomUUID();
        Order builtOrder = Order.builder()
                .id(orderId)
                .meja(meja)
                .build();

        assertEquals(orderId, builtOrder.getId());
        assertEquals(meja, builtOrder.getMeja());
    }
}