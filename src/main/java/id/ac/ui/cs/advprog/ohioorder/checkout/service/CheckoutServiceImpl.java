package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutResponse;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InsufficientQuantityException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.repository.CheckoutRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.client.MenuServiceClient;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderMapper;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import id.ac.ui.cs.advprog.ohioorder.pesanan.repository.OrderRepository;
import id.ac.ui.cs.advprog.ohioorder.pesanan.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@AllArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final OrderRepository orderRepository;
    private final MenuItemQuantityValidator quantityValidator;
    private final MenuServiceClient menuServiceClient;
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @Override
    public Checkout save(Checkout checkout) {
        return checkoutRepository.save(checkout);
    }

    @Override
    public Optional<Checkout> create(UUID orderId) {
        Checkout checkout = new Checkout();

        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isPresent()) {
            checkout.setOrder(order.get());
            return Optional.of(checkoutRepository.save(checkout));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Checkout> findById(String id) {
        return checkoutRepository.findById(UUID.fromString(id));
    }

    @Override
    public List<Checkout> findAll() {
        return checkoutRepository.findAllOrderByOrderCreatedAt();
    }

    @Override
    public List<CheckoutResponse> findAllFormatted() {
        return checkoutRepository.findAllOrderByOrderCreatedAt()
                .stream()
                .map(checkout -> {
                    OrderDto.OrderResponse orderResponse = orderMapper.toDto(checkout.getOrder());
                    CompletableFuture<OrderDto.OrderResponse> enrichedFuture = orderService.enrichOrderResponseAsync(orderResponse);
                    return enrichedFuture.thenApply(enriched -> {
                        CheckoutResponse response = new CheckoutResponse();
                        response.setCheckout(checkout);
                        response.setOrder(enriched);
                        return response;
                    });
                })
                .map(CompletableFuture::join)
                .toList();
    }

    public void validateQuantitiesBeforeNextState(Checkout checkout) throws InsufficientQuantityException {
        Order order = checkout.getOrder();
        List<String> validationErrors = quantityValidator.validateOrderItemsQuantity(order);

        if (!validationErrors.isEmpty()) {
            throw new InsufficientQuantityException(String.join("; ", validationErrors));
        }
    }

    public void reduceMenuItemQuantities(Checkout checkout) {
        Order order = checkout.getOrder();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        List<String> failedItems = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {
            CompletableFuture<Boolean> future = menuServiceClient.reduceMenuItemQuantityAsync(
                    item.getMenuItemId(), item.getQuantity());

            futures.add(future.thenApply(success -> {
                if (!success) {
                    failedItems.add(item.getMenuItemId());
                }
                return success;
            }));
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

            if (!failedItems.isEmpty()) {
                throw new RuntimeException("Failed to reduce quantities for menu items: " + String.join(", ", failedItems));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while reducing menu item quantities", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error reducing menu item quantities: " + e.getMessage(), e);
        }
    }
}