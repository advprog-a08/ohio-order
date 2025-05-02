package id.ac.ui.cs.advprog.ohioorder.checkout.service;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.repository.CheckoutRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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

    @Test
    void testFindByIdFound() {
        UUID id = UUID.randomUUID();
        Checkout checkout = new Checkout();
        doReturn(Optional.of(checkout)).when(checkoutRepository).findById(id);

        Optional<Checkout> findById = checkoutService.findById(String.valueOf(id));
        assertTrue(findById.isPresent());
        assertEquals(checkout, findById.get());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Checkout> findById = checkoutService.findById("3f0da82b-6cf4-44bd-bc26-f1303944e662");
        assertTrue(findById.isEmpty());
    }
}
