package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.pesanan.client.MenuServiceClient;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuServiceResponse;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class MenuItemQuantityValidator {
    private final MenuServiceClient menuServiceClient;

    public List<String> validateOrderItemsQuantity(Order order) {
        List<String> validationErrors = new ArrayList<>();
        List<CompletableFuture<Void>> validationFutures = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            CompletableFuture<Void> future = menuServiceClient.getMenuItemAsync(item.getMenuItemId())
                    .thenAccept(menuResponse -> validateMenuItemQuantity(menuResponse, item, validationErrors))
                    .exceptionally(ex -> {
                        validationErrors.add("Menu item not found: " + item.getMenuItemId());
                        return null;
                    });
            validationFutures.add(future);
        }

        try {
            CompletableFuture.allOf(validationFutures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException e) {
            validationErrors.add("Error validating menu items: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            validationErrors.add("Error validating menu items: " + e.getMessage());
        }

        return validationErrors;
    }

    private void validateMenuItemQuantity(MenuServiceResponse menuResponse, OrderItem item, List<String> validationErrors) {
        if (menuResponse == null || menuResponse.getData() == null) {
            validationErrors.add("Menu item not found: " + item.getMenuItemId());
            return;
        }

        Integer availableQuantity = menuResponse.getData().getQuantity();
        if (availableQuantity == null) {
            return;
        }

        if (availableQuantity < item.getQuantity()) {
            validationErrors.add(String.format(
                    "Insufficient quantity for menu item '%s'. Available: %d, Requested: %d",
                    menuResponse.getData().getName(), availableQuantity, item.getQuantity()
            ));
        }
    }
}