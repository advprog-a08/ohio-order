package id.ac.ui.cs.advprog.ohioorder.checkout.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CheckoutCreateRequestTest {

    @Test
    void testBuilderCreatesObjectCorrectly() {
        CheckoutCreateRequest request = CheckoutCreateRequest.builder()
                .orderId("ORD-123")
                .build();

        assertNotNull(request);
        assertEquals("ORD-123", request.getOrderId());
    }

    @Test
    void testSetterAndGetter() {
        CheckoutCreateRequest request = new CheckoutCreateRequest("ORD-456");

        assertEquals("ORD-456", request.getOrderId());
    }

    @Test
    void testEqualsAndHashCode() {
        CheckoutCreateRequest r1 = CheckoutCreateRequest.builder().orderId("ORD-789").build();
        CheckoutCreateRequest r2 = CheckoutCreateRequest.builder().orderId("ORD-789").build();

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}
