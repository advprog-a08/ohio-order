package id.ac.ui.cs.advprog.ohioorder.checkout.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutCreateRequestTest {

    @Test
    void testBuilderCreatesObjectCorrectly() {
        UUID orderId = UUID.randomUUID();
        CheckoutCreateRequest request = CheckoutCreateRequest.builder()
                .orderId(orderId)
                .build();

        assertNotNull(request);
        assertEquals(orderId, request.getOrderId());
    }

    @Test
    void testSetterAndGetter() {
        UUID orderId = UUID.randomUUID();
        CheckoutCreateRequest request = new CheckoutCreateRequest(orderId);

        assertEquals(orderId, request.getOrderId());
    }

    @Test
    void testEqualsAndHashCode() {
        UUID orderId = UUID.randomUUID();

        CheckoutCreateRequest r1 = CheckoutCreateRequest.builder().orderId(orderId).build();
        CheckoutCreateRequest r2 = CheckoutCreateRequest.builder().orderId(orderId).build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}
