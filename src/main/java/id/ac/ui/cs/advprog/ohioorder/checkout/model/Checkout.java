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

    @NotNull
    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    public Checkout() {
        this.state = CheckoutStateType.DRAFT;
    }

    @JsonIgnore
    public CheckoutState getCheckoutState() {
        return state.getCheckoutState();
    }

    public void nextState() {
        state.getCheckoutState().next(this);
    }

    public void update() {
        state.getCheckoutState().update();
    }

    public void cancel() throws InvalidStateTransitionException {
        state.getCheckoutState().cancel(this);
    }
}
