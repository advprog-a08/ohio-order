package id.ac.ui.cs.advprog.ohioorder.checkout.repository;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.meja.enums.MejaStatus;
import id.ac.ui.cs.advprog.ohioorder.meja.model.Meja;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("postgres-testcontainer")
public class CheckoutRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CheckoutRepository checkoutRepository;

    private Meja meja1;
    private Meja meja2;
    private Order order1;
    private Order order2;
    private Order order3;

    private String userId1 = "user-123";
    private String userId2 = "user-456";

    @BeforeEach
    void setUp() {
        // Create test data
        meja1 = Meja.builder()
                .nomorMeja("A1")
                .status(MejaStatus.TERISI)
                .build();

        meja2 = Meja.builder()
                .nomorMeja("A2")
                .status(MejaStatus.TERISI)
                .build();

        entityManager.persist(meja1);
        entityManager.persist(meja2);

        order1 = Order.builder()
                .meja(meja1)
                .build();

        order2 = Order.builder()
                .meja(meja2)
                .build();

        order3 = Order.builder()
                .meja(meja1)
                .build();
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.persist(order3);

        entityManager.flush();
    }

    @Test
    void testSaveWithoutOrderShouldFail() {
        Checkout checkout = new Checkout();
        assertThrows(Exception.class, () -> checkoutRepository.saveAndFlush(checkout));
    }

    @Test
    void testSaveWithOrderShouldSuccess() {
        Checkout checkout = new Checkout();
        checkout.setOrder(order1);

        checkoutRepository.saveAndFlush(checkout);

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
