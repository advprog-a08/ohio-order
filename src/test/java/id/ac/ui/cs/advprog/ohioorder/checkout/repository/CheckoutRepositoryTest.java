package id.ac.ui.cs.advprog.ohioorder.checkout.repository;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CheckoutRepositoryTest {

    @Autowired
    private CheckoutRepository checkoutRepository;

    @BeforeEach
    void setUp() {
        checkoutRepository.deleteAll();
    }

    @Test
    void testSaveAndFind() {
        Checkout checkout = new Checkout();
        checkoutRepository.save(checkout);

        Optional<Checkout> foundCheckout = checkoutRepository.findById(checkout.getId());
        assertTrue(foundCheckout.isPresent());
        assertEquals(checkout, foundCheckout.get());
    }

    @Test
    void testUpdate() {
        Checkout checkout = new Checkout();
        checkoutRepository.save(checkout);

        checkout.setState(CheckoutStateType.CANCELLED);
        checkoutRepository.save(checkout);

        Optional<Checkout> foundCheckout = checkoutRepository.findById(checkout.getId());
        assertTrue(foundCheckout.isPresent());
        assertEquals(checkout.getCheckoutState(), foundCheckout.get().getCheckoutState());
    }

    @Test
    void testDelete() {
        Checkout checkout = new Checkout();
        checkoutRepository.save(checkout);

        checkoutRepository.deleteById(checkout.getId());

        Optional<Checkout> foundCheckout = checkoutRepository.findById(checkout.getId());
        assertFalse(foundCheckout.isPresent());
    }
}
