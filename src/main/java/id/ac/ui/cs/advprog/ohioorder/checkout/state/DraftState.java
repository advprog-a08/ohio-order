package id.ac.ui.cs.advprog.ohioorder.checkout.state;

import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InsufficientQuantityException;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutServiceImpl;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class DraftState implements CheckoutState {
    private static final DraftState INSTANCE = new DraftState();
    private static ApplicationContext context;
    private DraftState() {}

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    public static DraftState getInstance() {
        return INSTANCE;
    }

    @Override
    public void advance(Checkout checkout) throws InvalidStateTransitionException {
        if (context != null) {
            CheckoutService checkoutService = context.getBean(CheckoutService.class);
            CheckoutServiceImpl checkoutServiceImpl = (CheckoutServiceImpl) checkoutService;

            try {
                checkoutServiceImpl.validateQuantitiesBeforeNextState(checkout);

                checkoutServiceImpl.reduceMenuItemQuantities(checkout);

                Order order = checkout.getOrder();
                order.setLocked(true);

                checkout.setState(CheckoutStateType.ORDERED);
            } catch (InsufficientQuantityException e) {
                throw new InvalidStateTransitionException("Cannot proceed to ORDERED state: " + e.getMessage());
            } catch (RuntimeException e) {
                throw new InvalidStateTransitionException("Failed to update menu item quantities: " + e.getMessage());
            }
        } else {
            checkout.setState(CheckoutStateType.ORDERED);
        }
    }

    @Override
    public void cancel(Checkout checkout) throws InvalidStateTransitionException {
        checkout.setState(CheckoutStateType.CANCELLED);
    }
}