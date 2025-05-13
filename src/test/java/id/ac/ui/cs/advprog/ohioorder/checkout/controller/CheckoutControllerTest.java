package id.ac.ui.cs.advprog.ohioorder.checkout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutCreateRequest;
import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CheckoutController.class)
@Import(CheckoutControllerTest.TestConfig.class)
class CheckoutControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CheckoutService checkoutService() {
            return mock(CheckoutService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private ObjectMapper objectMapper;

    private Checkout mockCheckout;
    private String validOrderId;

    @BeforeEach
    void setUp() {
        validOrderId = UUID.randomUUID().toString();
        mockCheckout = new Checkout();
        mockCheckout.setId(UUID.fromString(validOrderId));
    }

    @Test
    void findOne_shouldReturnCheckout_WhenFound() throws Exception {
        UUID checkoutId = UUID.randomUUID();

        Checkout checkout = new Checkout();
        checkout.setId(checkoutId);
        checkout.setState(CheckoutStateType.DRAFT);

        when(checkoutService.findById(checkoutId.toString())).thenReturn(Optional.of(checkout));

        mockMvc.perform(get("/api/checkout/{checkoutId}", checkoutId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(checkoutId.toString()))
                .andExpect(jsonPath("$.state").value("DRAFT"));
    }

    @Test
    void findOne_shouldReturnNotFound_whenNotFound() throws Exception {
        when(checkoutService.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/checkout/xyz789")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturnCheckout_whenValidOrderId() throws Exception {
        doReturn(Optional.of(mockCheckout)).when(checkoutService).create(validOrderId);

        String requestJson = objectMapper.writeValueAsString(CheckoutCreateRequest.builder()
                .orderId(validOrderId)
                .build());

        mockMvc.perform(post("/api/checkout/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validOrderId));
    }

    @Test
    void cancel_shouldReturn404_whenCheckoutNotFound() throws Exception {
        String orderId = UUID.randomUUID().toString();
        doReturn(Optional.empty()).when(checkoutService).findById(orderId);

        mockMvc.perform(delete("/api/checkout/cancel/{checkoutId}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancel_shouldReturn200_whenCheckoutSuccessfullyCanceled() throws Exception {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.DRAFT);
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        mockMvc.perform(delete("/api/checkout/cancel/{checkoutId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(CheckoutStateType.CANCELLED.toString()));
    }

    @Test
    void cancel_shouldReturn400_whenCheckoutAlreadyCancelled() throws Exception {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.CANCELLED);  // Already cancelled
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        mockMvc.perform(delete("/api/checkout/cancel/{checkoutId}", orderId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void next_shouldReturn200_whenNextIsSuccessful() throws Exception {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.DRAFT);
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        mockMvc.perform(post("/api/checkout/next/{checkoutId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(CheckoutStateType.ORDERED.toString()));
    }

    @Test
    void next_shouldReturn400_whenNextIsNotSuccessful() throws Exception {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.CANCELLED);  // Cancelled cannot be next'd
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        mockMvc.perform(post("/api/checkout/next/{checkoutId}", orderId))
                .andExpect(status().isBadRequest());
    }
}
