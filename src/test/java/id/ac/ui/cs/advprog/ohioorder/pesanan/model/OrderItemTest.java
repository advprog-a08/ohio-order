package id.ac.ui.cs.advprog.ohioorder.pesanan.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    private UUID orderItemId;
    private OrderItem orderItem;
    private UUID orderId;
    private Order order;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        orderItemId = UUID.randomUUID();

        order = Order.builder().id(orderId).build();

        orderItem = OrderItem.builder()
                .id(orderItemId)
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .order(order)
                .build();
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        assertEquals(orderItemId, orderItem.getId());
        assertEquals("menu-1", orderItem.getMenuItemId());
        assertEquals("Burger", orderItem.getMenuItemName());
        assertEquals(50000.0, orderItem.getPrice());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(order, orderItem.getOrder());

        UUID newOrderId = UUID.randomUUID();
        Order newOrder = Order.builder().id(newOrderId).build();

        UUID updatedOrderId = UUID.randomUUID();
        orderItem.setId(updatedOrderId);
        orderItem.setMenuItemId("menu-updated");
        orderItem.setMenuItemName("Updated Burger");
        orderItem.setPrice(60000.0);
        orderItem.setQuantity(3);
        orderItem.setOrder(newOrder);

        assertEquals(updatedOrderId, orderItem.getId());
        assertEquals("menu-updated", orderItem.getMenuItemId());
        assertEquals("Updated Burger", orderItem.getMenuItemName());
        assertEquals(60000.0, orderItem.getPrice());
        assertEquals(3, orderItem.getQuantity());
        assertEquals(newOrder, orderItem.getOrder());
    }

    @Test
    void builderPattern_CreatesOrderItemCorrectly() {
        UUID itemId = UUID.randomUUID();
        OrderItem builtItem = OrderItem.builder()
                .id(itemId)
                .menuItemId("menu-test")
                .menuItemName("Test Food")
                .price(25000.0)
                .quantity(4)
                .order(order)
                .build();

        assertEquals(itemId, builtItem.getId());
        assertEquals("menu-test", builtItem.getMenuItemId());
        assertEquals("Test Food", builtItem.getMenuItemName());
        assertEquals(25000.0, builtItem.getPrice());
        assertEquals(4, builtItem.getQuantity());
        assertEquals(order, builtItem.getOrder());
    }

    @Test
    void noArgsConstructor_CreatesEmptyOrderItem() {
        OrderItem emptyItem = new OrderItem();

        assertNull(emptyItem.getId());
        assertNull(emptyItem.getMenuItemId());
        assertNull(emptyItem.getMenuItemName());
        assertEquals(0.0, emptyItem.getPrice());
        assertEquals(0, emptyItem.getQuantity());
        assertNull(emptyItem.getOrder());
    }

    @Test
    void allArgsConstructor_CreatesFullOrderItem() {
        UUID itemId = UUID.randomUUID();
        OrderItem fullItem = OrderItem.builder()
                .id(itemId)
                .menuItemId("menu-full")
                .menuItemName("Full Item")
                .price(35000.0)
                .quantity(5)
                .order(order)
                .build();

        assertEquals(itemId, fullItem.getId());
        assertEquals("menu-full", fullItem.getMenuItemId());
        assertEquals("Full Item", fullItem.getMenuItemName());
        assertEquals(35000.0, fullItem.getPrice());
        assertEquals(5, fullItem.getQuantity());
        assertEquals(order, fullItem.getOrder());
    }
}