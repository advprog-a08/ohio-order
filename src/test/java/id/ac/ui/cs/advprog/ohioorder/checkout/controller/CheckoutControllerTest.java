package id.ac.ui.cs.advprog.ohioorder.checkout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.ohioorder.checkout.dto.CheckoutCreateRequest;
import id.ac.ui.cs.advprog.ohioorder.checkout.enums.CheckoutStateType;
import id.ac.ui.cs.advprog.ohioorder.checkout.model.Checkout;
import id.ac.ui.cs.advprog.ohioorder.checkout.service.CheckoutService;
import id.ac.ui.cs.advprog.ohioorder.grpc.AdminGrpcClient;
import id.ac.ui.cs.advprog.ohioorder.interceptor.AuthInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CheckoutController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        CheckoutControllerTest.NoSecurityConfig.class,
        CheckoutControllerTest.TestConfig.class,
        CheckoutControllerTest.MockGrpcClientConfig.class,
        CheckoutControllerTest.MockInterceptorConfig.class
})
class CheckoutControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CheckoutService checkoutService() {
            return mock(CheckoutService.class);
        }
    }

    @TestConfiguration
    static class MockGrpcClientConfig {
        @Bean
        public AdminGrpcClient adminGrpcClient() {
            return mock(AdminGrpcClient.class);
        }
    }

    @TestConfiguration
    static class MockInterceptorConfig {
        @Bean
        public AuthInterceptor authInterceptor() {
            return mock(AuthInterceptor.class);
        }
    }

    @TestConfiguration
    static class NoSecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .cors(AbstractHttpConfigurer::disable).
                    authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthInterceptor authInterceptor;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private ObjectMapper objectMapper;

    private Checkout mockCheckout;
    private UUID validOrderId;

    @BeforeEach
    void setUp() throws Exception {
        validOrderId = UUID.randomUUID();
        mockCheckout = new Checkout();
        mockCheckout.setId(validOrderId);

        // Let all requests through
        doAnswer(invocation -> true)
                .when(authInterceptor)
                .preHandle(any(), any(), any());
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

        mockMvc.perform(post("/api/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(validOrderId.toString()));
    }

    @Test
    void findAll_shouldReturnAllCheckout() throws Exception {
        doReturn(List.of(mockCheckout)).when(checkoutService).findAll();

        mockMvc.perform(get("/api/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(mockCheckout.getId().toString()))
                .andExpect(jsonPath("$[0].state").value("DRAFT"));
    }

    @Test
    void cancel_shouldReturn404_whenCheckoutNotFound() throws Exception {
        String orderId = UUID.randomUUID().toString();
        doReturn(Optional.empty()).when(checkoutService).findById(orderId);

        mockMvc.perform(delete("/api/checkout/{checkoutId}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancel_shouldReturn200_whenCheckoutSuccessfullyCanceled() throws Exception {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.DRAFT);
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        mockMvc.perform(delete("/api/checkout/{checkoutId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(CheckoutStateType.CANCELLED.toString()));
    }

    @Test
    void cancel_shouldReturn400_whenCheckoutAlreadyCancelled() throws Exception {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.CANCELLED);  // Already cancelled
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        mockMvc.perform(delete("/api/checkout/{checkoutId}", orderId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void advance_shouldReturn200_whenNextIsSuccessful() throws Exception {
        String checkoutId = UUID.randomUUID().toString();

        Checkout spyCheckout = spy(new Checkout());
        spyCheckout.setState(CheckoutStateType.DRAFT);

        doAnswer(invocation -> {
            spyCheckout.setState(CheckoutStateType.ORDERED);
            return null;
        }).when(spyCheckout).advance();

        doReturn(Optional.of(spyCheckout)).when(checkoutService).findById(checkoutId);

        mockMvc.perform(post("/api/checkout/{checkoutId}/advance", checkoutId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value(CheckoutStateType.ORDERED.toString()))
                .andExpect(jsonPath("$.message").value(CheckoutStateType.ORDERED.getCheckoutState().message()));
    }

    @Test
    void advance_shouldReturn400_whenNextIsNotSuccessful() throws Exception {
        String orderId = UUID.randomUUID().toString();

        mockCheckout.setState(CheckoutStateType.CANCELLED);  // Cancelled cannot be advanced
        doReturn(Optional.of(mockCheckout)).when(checkoutService).findById(orderId);

        mockMvc.perform(post("/api/checkout/{checkoutId}/advance", orderId))
                .andExpect(status().isBadRequest());
    }
}
