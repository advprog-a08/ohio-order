package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.repository.CheckoutRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CheckoutServiceTest {
    @Mock
    private CheckoutRepository checkoutRepository;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    @Test
    void testCreate() {
        Checkout checkout = new Checkout();
        doReturn(checkout).when(checkoutRepository).save(any(Checkout.class));

        Checkout created = checkoutService.create();
        assertEquals(checkout.getId(), created.getId());
    }
}
