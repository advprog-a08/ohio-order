package id.ac.ui.cs.advprog.ohioorder.checkout.model;

import java.util.UUID;

import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
