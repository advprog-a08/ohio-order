package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.pesanan.client.MenuServiceClient;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuItemDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuServiceResponse;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemQuantityValidatorTest {

    @Mock
    private MenuServiceClient menuServiceClient;

    @InjectMocks
    private MenuItemQuantityValidator validator;

    private Order order;
    private MenuServiceResponse menuResponse1;
    private MenuServiceResponse menuResponse2;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setOrderItems(new ArrayList<>());

        OrderItem orderItem1 = OrderItem.builder()
                .menuItemId("menu-1")
                .quantity(2)
                .build();

        OrderItem orderItem2 = OrderItem.builder()
                .menuItemId("menu-2")
                .quantity(3)
                .build();

        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        // Setup menu responses
        MenuItemDto menuItemDto1 = MenuItemDto.builder()
                .id("menu-1")
                .name("Burger")
                .quantity(5) // Enough quantity
                .build();

        MenuItemDto menuItemDto2 = MenuItemDto.builder()
                .id("menu-2")
                .name("Pizza")
                .quantity(2) // Insufficient quantity
                .build();

        menuResponse1 = new MenuServiceResponse();
        menuResponse1.setSuccess(true);
        menuResponse1.setData(menuItemDto1);

        menuResponse2 = new MenuServiceResponse();
        menuResponse2.setSuccess(true);
        menuResponse2.setData(menuItemDto2);
    }

    @Test
    void validateOrderItemsQuantity_ShouldReturnNoErrors_WhenAllItemsHaveSufficientQuantity() {
        // Mock both menu items having sufficient quantity
        MenuItemDto menuItemDto2 = MenuItemDto.builder()
                .id("menu-2")
                .name("Pizza")
                .quantity(5) // Now has enough quantity
                .build();

        menuResponse2.setData(menuItemDto2);

        when(menuServiceClient.getMenuItemAsync("menu-1"))
                .thenReturn(CompletableFuture.completedFuture(menuResponse1));
        when(menuServiceClient.getMenuItemAsync("menu-2"))
                .thenReturn(CompletableFuture.completedFuture(menuResponse2));

        List<String> errors = validator.validateOrderItemsQuantity(order);

        assertTrue(errors.isEmpty());
    }

    @Test
    void validateOrderItemsQuantity_ShouldReturnErrors_WhenSomeItemsHaveInsufficientQuantity() {
        when(menuServiceClient.getMenuItemAsync("menu-1"))
                .thenReturn(CompletableFuture.completedFuture(menuResponse1));
        when(menuServiceClient.getMenuItemAsync("menu-2"))
                .thenReturn(CompletableFuture.completedFuture(menuResponse2));

        List<String> errors = validator.validateOrderItemsQuantity(order);

        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().contains("Insufficient quantity for menu item 'Pizza'"));
    }

    @Test
    void validateOrderItemsQuantity_ShouldReturnErrors_WhenMenuItemNotFound() {
        when(menuServiceClient.getMenuItemAsync("menu-1"))
                .thenReturn(CompletableFuture.completedFuture(menuResponse1));
        when(menuServiceClient.getMenuItemAsync("menu-2"))
                .thenReturn(CompletableFuture.failedFuture(new NoSuchElementException("Menu item not found")));

        List<String> errors = validator.validateOrderItemsQuantity(order);

        assertEquals(1, errors.size());
        assertTrue(errors.getFirst().contains("Menu item not found: menu-2"));
    }
}