package com.amigoscode.testing.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest( // allows us to test database queries
    properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none" // will trigger jpa annotations (@Column(nullable = false) when unit testing
    }
)
public class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    void itShouldInsertPayment() {
        // Given
        long paymentId = 1L;
        Payment payment = new Payment(
            null,
            UUID.randomUUID(),
            new BigDecimal("10.00"),
            Currency.USD, "card123",
            "Donation");
        // When
        paymentRepository.save(payment);

        // Then
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        assertThat(paymentOptional)
            .isPresent()
            .hasValueSatisfying(p -> assertThat(p).isEqualTo(payment));
    }

}
