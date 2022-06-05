package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest // makes sure that we run this class the entire application will start up
@AutoConfigureMockMvc
public class PaymentsIntegrationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MockMvc mockMvc;

    /*
    *   Tests end to end customer registration and payment transaction end to end
    * */
    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {
        // Given a customer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "James", "+447000000000");
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        // mock the api call
        ResultActions customerRegResultActions  = mockMvc.perform(MockMvcRequestBuilders
            .put("/api/v1/customer-registration") // endpoint and HTTP type
            .contentType(MediaType.APPLICATION_JSON) // payload type
            .content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))) // request payload
        );

        // ... Payment
        long paymentId = 1L;

        Payment payment = new Payment(
            paymentId,
            customerId,
            new BigDecimal("100.00"),
            Currency.GBP,
            "x0x0x0x0",
            "Zakat"
        );

        // payment request
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        // When payment endpoint is mocked
        ResultActions paymentResultActions  = mockMvc.perform(MockMvcRequestBuilders
            .put("/api/v1/payment") // endpoint and HTTP type
            .contentType(MediaType.APPLICATION_JSON) // payload type
            .content(Objects.requireNonNull(objectToJson(paymentRequest))) // request payload
        );

        // Then both customer registration and payment requests are 200 status code
        customerRegResultActions.andExpect(status().isOk()); // make sure the request went out fine
        paymentResultActions.andExpect(status().isOk());

        // Payment is stored in db
        // TODO: Do not use paymentRepository instead create an endpoint to retrieve payments for customers
        assertThat(paymentRepository.findById(paymentId))
            .isPresent()
            .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to json");
            return null;
        }
    }
}
