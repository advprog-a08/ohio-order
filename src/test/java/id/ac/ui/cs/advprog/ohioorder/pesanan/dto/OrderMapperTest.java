package id.ac.ui.cs.advprog.ohioorder.pesanan.dto;

import id.ac.ui.cs.advprog.ohioorder.meja.dto.MejaResponse;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.meja.service.MejaService;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderMapperTest {

    @Mock
    private MejaService mejaService;

    @InjectMocks
    private OrderMapper orderMapper;

    private UUID mejaId;
    private OrderDto.OrderRequest orderRequest;
    private Order order;
    private OrderItem orderItem;
    private Meja meja;
    private MejaResponse mejaResponse;

    @BeforeEach
    void setUp() {
        mejaId = UUID.randomUUID();

        meja = Meja.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();

        mejaResponse = MejaResponse.builder()
                .id(mejaId)
                .nomorMeja("A1")
                .status(MejaStatus.TERSEDIA)
                .build();

        orderRequest = OrderDto.OrderRequest.builder()
                .mejaId(mejaId)
                .items(List.of(
                        OrderDto.OrderItemRequest.builder()
                                .menuItemId("menu-1")
                                .menuItemName("Burger")
                                .price(50000.0)
                                .quantity(2)
                                .build()
                ))
                .build();

        orderItem = OrderItem.builder()
                .id("item-1")
                .menuItemId("menu-1")
                .menuItemName("Burger")
                .price(50000.0)
                .quantity(2)
                .build();

        order = Order.builder()
                .id("order-123")
                .meja(meja)
                .orderItems(new ArrayList<>(List.of(orderItem)))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        orderItem.setOrder(order);
    }

    @Test
    void toEntity_Success() {
        when(mejaService.getMejaById(mejaId)).thenReturn(mejaResponse);

        Order result = orderMapper.toEntity(orderRequest);

        assertNotNull(result);
        assertEquals(mejaId, result.getMeja().getId());
        assertEquals("A1", result.getMeja().getNomorMeja());
        assertEquals(MejaStatus.TERSEDIA, result.getMeja().getStatus());

        assertNotNull(result.getOrderItems());
        assertEquals(1, result.getOrderItems().size());

        OrderItem resultItem = result.getOrderItems().getFirst();
        assertEquals("menu-1", resultItem.getMenuItemId());
        assertEquals("Burger", resultItem.getMenuItemName());
        assertEquals(50000.0, resultItem.getPrice());
        assertEquals(2, resultItem.getQuantity());
    }

    @Test
    void toEntity_WithNullItems_Success() {
        orderRequest.setItems(null);
        when(mejaService.getMejaById(mejaId)).thenReturn(mejaResponse);

        Order result = orderMapper.toEntity(orderRequest);

        assertNotNull(result);
        assertEquals(mejaId, result.getMeja().getId());
        assertTrue(result.getOrderItems().isEmpty());
    }

    @Test
    void toDto_Order_Success() {
        OrderDto.OrderResponse result = orderMapper.toDto(order);

        assertNotNull(result);
        assertEquals(order.getId(), result.getId());
        assertEquals(order.getMeja().getId(), result.getMejaId());
        assertEquals(order.getMeja().getNomorMeja(), result.getNomorMeja());
        assertEquals(order.getCreatedAt(), result.getCreatedAt());
        assertEquals(order.getUpdatedAt(), result.getUpdatedAt());
        assertEquals(100000.0, result.getTotal()); // 50000 * 2

        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().size());

        OrderDto.OrderItemResponse itemResponse = result.getItems().getFirst();
        assertEquals("item-1", itemResponse.getId());
        assertEquals("menu-1", itemResponse.getMenuItemId());
        assertEquals("Burger", itemResponse.getMenuItemName());
        assertEquals(50000.0, itemResponse.getPrice());
        assertEquals(2, itemResponse.getQuantity());
        assertEquals(100000.0, itemResponse.getSubtotal());
    }

    @Test
    void toDto_OrderItem_Success() {
        OrderDto.OrderItemResponse result = orderMapper.toDto(orderItem);

        assertNotNull(result);
        assertEquals(orderItem.getId(), result.getId());
        assertEquals(orderItem.getMenuItemId(), result.getMenuItemId());
        assertEquals(orderItem.getMenuItemName(), result.getMenuItemName());
        assertEquals(orderItem.getPrice(), result.getPrice());
        assertEquals(orderItem.getQuantity(), result.getQuantity());
        assertEquals(orderItem.getPrice() * orderItem.getQuantity(), result.getSubtotal());
    }
}