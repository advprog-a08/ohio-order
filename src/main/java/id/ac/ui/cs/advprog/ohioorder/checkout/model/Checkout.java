package id.ac.ui.cs.advprog.ohioorder.checkout.model;

import java.util.UUID;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CheckoutState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.DraftState;
import id.ac.ui.cs.advprog.ohioorder.checkout.utils.CheckoutStateMachine;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Checkout {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; 

    @Enumerated(EnumType.STRING)
    private CheckoutStateType state;

    @Transient
    private CheckoutState checkoutState;

    public Checkout() {
        this.state = CheckoutStateType.DRAFT;
        this.checkoutState = DraftState.getInstance();
    }

    @PostLoad
    public void initializeState() {
        this.checkoutState = CheckoutStateMachine.getStateForStatus(state);
    }

    public void nextState() {
        checkoutState.next(this);
    }

    public void cancel() {
        checkoutState.cancel(this);
    }
}
