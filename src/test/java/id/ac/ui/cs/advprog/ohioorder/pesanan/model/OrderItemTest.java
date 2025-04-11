package id.ac.ui.cs.advprog.ohioorder.pesanan.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    private OrderItem orderItem;
    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId("order-123");

        orderItem = OrderItem.builder()
                .id("item-1")
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .order(order)
                .build();
    }

    @Test
    void gettersAndSetters_WorkCorrectly() {
        // Act & Assert
        assertEquals("item-1", orderItem.getId());
        assertEquals("menu-1", orderItem.getMenuItemId());
        assertEquals("Burger", orderItem.getMenuItemName());
        assertEquals(50000.0, orderItem.getPrice());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(order, orderItem.getOrder());

        // Test setters
        Order newOrder = new Order();
        newOrder.setId("order-456");

        orderItem.setId("item-updated");
        orderItem.setMenuItemId("menu-updated");
        orderItem.setMenuItemName("Updated Burger");
        orderItem.setPrice(60000.0);
        orderItem.setQuantity(3);
        orderItem.setOrder(newOrder);

        assertEquals("item-updated", orderItem.getId());
        assertEquals("menu-updated", orderItem.getMenuItemId());
        assertEquals("Updated Burger", orderItem.getMenuItemName());
        assertEquals(60000.0, orderItem.getPrice());
        assertEquals(3, orderItem.getQuantity());
        assertEquals(newOrder, orderItem.getOrder());
    }

    @Test
    void builderPattern_CreatesOrderItemCorrectly() {
        // Arrange & Act
        OrderItem builtItem = OrderItem.builder()
                .id("item-test")
                .menuItemId("menu-test")
                .menuItemName("Test Food")
                .price(25000.0)
                .quantity(4)
                .order(order)
                .build();

        // Assert
        assertEquals("item-test", builtItem.getId());
        assertEquals("menu-test", builtItem.getMenuItemId());
        assertEquals("Test Food", builtItem.getMenuItemName());
        assertEquals(25000.0, builtItem.getPrice());
        assertEquals(4, builtItem.getQuantity());
        assertEquals(order, builtItem.getOrder());
    }

    @Test
    void noArgsConstructor_CreatesEmptyOrderItem() {
        // Arrange & Act
        OrderItem emptyItem = new OrderItem();

        // Assert
        assertNull(emptyItem.getId());
        assertNull(emptyItem.getMenuItemId());
        assertNull(emptyItem.getMenuItemName());
        assertEquals(0.0, emptyItem.getPrice());
        assertEquals(0, emptyItem.getQuantity());
        assertNull(emptyItem.getOrder());
    }

    @Test
    void allArgsConstructor_CreatesFullOrderItem() {
        // Arrange & Act
        OrderItem fullItem = new OrderItem(
                "item-full",
                "menu-full",
                "Full Item",
                35000.0,
                5,
                order
        );

        // Assert
        assertEquals("item-full", fullItem.getId());
        assertEquals("menu-full", fullItem.getMenuItemId());
        assertEquals("Full Item", fullItem.getMenuItemName());
        assertEquals(35000.0, fullItem.getPrice());
        assertEquals(5, fullItem.getQuantity());
        assertEquals(order, fullItem.getOrder());
    }
}