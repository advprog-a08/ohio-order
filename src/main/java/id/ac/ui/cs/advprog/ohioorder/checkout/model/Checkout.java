package id.ac.ui.cs.advprog.ohioorder.checkout.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CheckoutState;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.DraftState;
import id.ac.ui.cs.advprog.ohioorder.pesanan.model.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "checkout")
public class Checkout {
    @Id
    @GeneratedValue
    private UUID id; 

    @Enumerated(EnumType.STRING)
    private CheckoutStateType state;

    @Transient
    @JsonIgnore
    private CheckoutState checkoutState;

    @NotNull
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    public Checkout() {
        this.state = CheckoutStateType.DRAFT;
        this.checkoutState = DraftState.getInstance();
    }

    @PostLoad
    public void initializeState() {
        this.checkoutState = state.getCheckoutState();
    }

    public void setState(CheckoutStateType state) {
        this.state = state;
        this.initializeState();
    }

    public void nextState() {
        checkoutState.next(this);
    }

    public void update() {
        checkoutState.update();
    }

    public void cancel() throws InvalidStateTransitionException {
        checkoutState.cancel(this);
    }

    public double calculateTotal() {
        return order.calculateTotal();
    }
}
