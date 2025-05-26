package id.ac.ui.cs.advprog.ohioorder.checkout.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.exception.InvalidStateTransitionException;
import id.ac.ui.cs.advprog.ohioorder.checkout.state.CheckoutState;
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
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    @JsonProperty("message")
    public String message() {
        return state.getCheckoutState().message();
    }

    public Checkout() {
        this.state = CheckoutStateType.DRAFT;
    }

    @JsonIgnore
    public CheckoutState getCheckoutState() {
        return state.getCheckoutState();
    }

    public void advance() {
        state.getCheckoutState().advance(this);
    }

    public void cancel() throws InvalidStateTransitionException {
        state.getCheckoutState().cancel(this);
    }
}
