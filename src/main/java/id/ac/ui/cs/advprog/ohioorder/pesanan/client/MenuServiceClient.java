package id.ac.ui.cs.advprog.ohioorder.pesanan.client;

import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.MenuServiceResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.core.ParameterizedTypeReference;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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

    // Fetch multiple menu items in parallel
    public CompletableFuture<List<MenuServiceResponse>> getMultipleMenuItemsAsync(List<String> menuItemIds) {
        List<CompletableFuture<MenuServiceResponse>> futures = 
            menuItemIds.stream()
                .map(this::getMenuItemAsync)
                .toList();
        
        CompletableFuture<Void> allFutures = 
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        
        return allFutures.thenApply(v -> 
            futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList())
        );
    }

    public CompletableFuture<Boolean> reduceMenuItemQuantityAsync(String menuItemId, int quantity) {
        return webClient.put()
                .uri("/api/menus/reduce/{id}", menuItemId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BigDecimal(quantity))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                .map(ApiResponse::isSuccess)
                .onErrorResume(e -> {
                    // Log error for debugging
                    System.err.println("Error reducing quantity for menu item " + menuItemId + ": " + e.getMessage());
                    return Mono.just(false);
                })
                .toFuture();
    }

    @Setter
    @Getter
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

    }

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
}