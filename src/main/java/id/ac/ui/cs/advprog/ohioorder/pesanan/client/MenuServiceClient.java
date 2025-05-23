package id.ac.ui.cs.advprog.ohioorder.pesanan.client;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuServiceResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class MenuServiceClient {
    private final WebClient webClient;
    private final String menuServiceBaseUrl;

    public MenuServiceClient(WebClient.Builder webClientBuilder) {
        String menuServiceBaseUrl1;
        menuServiceBaseUrl1 = System.getProperty("MENU_SERVICE_URL");

        if (menuServiceBaseUrl1 == null || menuServiceBaseUrl1.isEmpty()) {
            menuServiceBaseUrl1 = "";
        }
        this.menuServiceBaseUrl = menuServiceBaseUrl1;
        this.webClient = webClientBuilder.baseUrl(menuServiceBaseUrl1).build();
    }

    // Asynchronous method to get a menu item
    public CompletableFuture<MenuServiceResponse> getMenuItemAsync(String menuItemId) {
        return webClient.get()
                .uri("/api/menus/{id}", menuItemId)
                .retrieve()
                .bodyToMono(MenuServiceResponse.class)
                .onErrorResume(e -> Mono.error(new NoSuchElementException("Menu item not found with ID: " + menuItemId)))
                .toFuture();
    }

    // Asynchronous method to verify a menu item exists
    public CompletableFuture<Boolean> verifyMenuItemExistsAsync(String menuItemId) {
        return getMenuItemAsync(menuItemId)
                .thenApply(response -> true)
                .exceptionally(ex -> false);
    }

    // Fetch multiple menu items in parallel
    public CompletableFuture<List<MenuServiceResponse>> getMultipleMenuItemsAsync(List<String> menuItemIds) {
        List<CompletableFuture<MenuServiceResponse>> futures = 
            menuItemIds.stream()
                .map(this::getMenuItemAsync)
                .collect(Collectors.toList());
        
        CompletableFuture<Void> allFutures = 
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        
        return allFutures.thenApply(v -> 
            futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );
    }
    
    // Original sync method for backward compatibility
    public MenuServiceResponse getMenuItem(String menuItemId) {
        try {
            return getMenuItemAsync(menuItemId).join();
        } catch (Exception e) {
            if (e.getCause() instanceof NoSuchElementException) {
                throw (NoSuchElementException) e.getCause();
            }
            throw new RuntimeException("Error fetching menu item", e);
        }
    }

    // Original sync method for backward compatibility
    public boolean verifyMenuItemExists(String menuItemId) {
        try {
            return verifyMenuItemExistsAsync(menuItemId).join();
        } catch (Exception e) {
            return false;
        }
    }
}