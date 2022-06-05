package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

public class PaymentServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        // mock the repo's in the payment constructor
        MockitoAnnotations.initMocks(this);
        paymentService = new PaymentService(customerRepository, paymentRepository, cardPaymentCharger);
    }

    @Test
    void itShouldChargeCardSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class))); // mock the customer repo that it/ll return a valid customer

        PaymentRequest paymentRequest = new PaymentRequest(
            new Payment(
                null,
                null, // set to null since in the service we set it at the end of the method which will need to be tested
                new BigDecimal("100.00"),
                Currency.USD,
                "card123xx",
                "Donation"
            )
        );

        // mock card charger
        given(cardPaymentCharger.chargeCard(
            paymentRequest.getPayment().getSource(),
            paymentRequest.getPayment().getAmount(),
            paymentRequest.getPayment().getCurrency(),
            paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(true));

        // When
        paymentService.chargeCard(customerId, paymentRequest);

        // Then - check if payment repo argument is being mocked saved
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentArgumentCaptorValue = paymentArgumentCaptor.getValue();

        // check if the captured argument value is equal to payment request but ignore the null customerId field
        // since we declared it null above
        assertThat(paymentArgumentCaptorValue)
            .isEqualToIgnoringGivenFields(paymentRequest.getPayment(), "customerId");
        assertThat(paymentArgumentCaptorValue.getCustomerId()).isEqualTo(customerId);
    }

    @Test
    void itShouldThrowWhenCardIsNotCharged() {
        // Given
        UUID customerId = UUID.randomUUID();
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class))); // mock the customer repo that it/ll return a valid customer

        PaymentRequest paymentRequest = new PaymentRequest(
            new Payment(
                null,
                null, // set to null since in the service we set it at the end of the method which will need to be tested
                new BigDecimal("100.00"),
                Currency.USD,
                "card123xx",
                "Donation"
            )
        );

        // mock card charger
        given(cardPaymentCharger.chargeCard(
            paymentRequest.getPayment().getSource(),
            paymentRequest.getPayment().getAmount(),
            paymentRequest.getPayment().getCurrency(),
            paymentRequest.getPayment().getDescription()
        )).willReturn(new CardPaymentCharge(false));

        // When
        assertThatThrownBy(() -> paymentService.chargeCard(customerId, paymentRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(String.format("Card not debited for customer %s", customerId));

        // Then
        then(paymentRepository).should(never()).save(any(Payment.class)); // check that this should not be invoked
    }

    @Test
    void itShouldNotChargeCardAndThrowWhenCurrencyNotSupported() {
        // Given
        UUID customerId = UUID.randomUUID();

        // ... Customer exists
        given(customerRepository.findById(customerId)).willReturn(Optional.of(mock(Customer.class)));

        // ... Euros
        Currency currency = Currency.EUR;

        // ... Payment request
        PaymentRequest paymentRequest = new PaymentRequest(
            new Payment(
                null,
                null,
                new BigDecimal("100.00"),
                currency,
                "card123xx",
                "Donation"
            )
        );

        // When
        assertThatThrownBy(() -> paymentService.chargeCard(customerId, paymentRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(String.format(
                "Currency [%s] not suported",
                paymentRequest.getPayment().getCurrency()));

        // Then

        // ... No interaction with cardPaymentCharger
        then(cardPaymentCharger).shouldHaveNoInteractions();

        // ... No interaction with paymentRepository
        then(paymentRepository).shouldHaveNoInteractions();
    }

    @Test
    void itShouldNotChargeAndThrowWhenCustomerNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();

        // Customer not found in db
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> paymentService.chargeCard(customerId, new PaymentRequest(new Payment())))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(String.format("Customer with id [%s] not found", customerId));

        // ... No interactions with PaymentCharger not PaymentRepository
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
    }
}
