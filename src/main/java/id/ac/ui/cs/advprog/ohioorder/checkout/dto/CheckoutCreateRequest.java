package id.ac.ui.cs.advprog.ohioorder.checkout.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CheckoutCreateRequest {
    private UUID orderId;
}
