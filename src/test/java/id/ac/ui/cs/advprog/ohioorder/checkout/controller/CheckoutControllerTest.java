package id.ac.ui.cs.advprog.ohioorder.checkout.controller;

import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutCreateRequest;
import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
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

        CheckoutCreateRequest request = CheckoutCreateRequest.builder().orderId(VALID_ORDER_ID).build();
        ResponseEntity<Checkout> response = checkoutController.create(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mockCheckout, response.getBody());

        verify(checkoutService, times(1)).create(VALID_ORDER_ID);
    }

    @Test
    void cancel_shouldReturn404_whenCheckoutNotFound() {
        String orderId = UUID.randomUUID().toString();
        doReturn(Optional.empty()).when(checkoutService).findById(orderId);

        ResponseEntity<Checkout> response = checkoutController.cancel(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Checkout not found should result in 404");
        verify(checkoutService, times(1)).findById(orderId);
    }

    @Test
    void cancel_shouldReturn200_whenCheckoutSuccessfullyCanceled() {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.DRAFT);
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        ResponseEntity<Checkout> response = checkoutController.cancel(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode(), "Request should succeed by returning a 200 status");
        assertEquals(CheckoutStateType.CANCELLED, mockCheckout.getState(), "Checkout state should be updated to CANCELLED");
    }

    @Test
    void cancel_shouldReturn400_whenCheckoutCannotBeCanceled() {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.CANCELLED); // CANCELLED state cannot be cancelled
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        assertThrows(InvalidStateTransitionException.class, () -> checkoutController.cancel(orderId));

//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(), "Request should fail by returning a 400 status");
//        assertEquals(CheckoutStateType.CANCELLED, mockCheckout.getState(), "Checkout state should not change");
    }
}
