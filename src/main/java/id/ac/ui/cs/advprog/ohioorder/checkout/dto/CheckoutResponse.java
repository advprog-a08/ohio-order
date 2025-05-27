package id.ac.ui.cs.advprog.ohioorder.checkout.dto;

import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.pesanan.dto.OrderDto;
import lombok.Data;

@Data
public class CheckoutResponse {
    Checkout checkout;
    OrderDto.OrderResponse order;
}
