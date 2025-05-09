package id.ac.ui.cs.advprog.ohioorder.checkout.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutCreateRequest {
    private String orderId;
}
