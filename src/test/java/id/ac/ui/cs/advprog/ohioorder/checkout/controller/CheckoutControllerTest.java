package id.ac.ui.cs.advprog.ohioorder.checkout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutControllerTest {

    @Mock
    private CheckoutService checkoutService;

    @InjectMocks
    private CheckoutController checkoutController;

    private Checkout mockCheckout;
    private final String VALID_ORDER_ID = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        mockCheckout = new Checkout();
        mockCheckout.setId(UUID.fromString(VALID_ORDER_ID));
    }

    @Test
    void returnsCheckoutWhenCreateWithValidOrderId() {
        doReturn(Optional.of(mockCheckout)).when(checkoutService).create(VALID_ORDER_ID);

        ResponseEntity<Checkout> response = checkoutController.create(VALID_ORDER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCheckout, response.getBody());

        verify(checkoutService, times(1)).create(VALID_ORDER_ID);
    }
}
